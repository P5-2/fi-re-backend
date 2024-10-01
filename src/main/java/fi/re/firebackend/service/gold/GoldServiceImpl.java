package fi.re.firebackend.service.gold;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.util.api.GoldInfoApi;
import fi.re.firebackend.util.api.JsonConverter;
import fi.re.firebackend.util.dateUtil.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class GoldServiceImpl implements GoldService {

    private static final Logger log = Logger.getLogger(GoldServiceImpl.class);
    private final GoldDao goldDao;
    private final GoldInfoApi goldInfoApi;

    public GoldServiceImpl(GoldDao goldDao, GoldInfoApi goldInfoApi) {
        this.goldDao = goldDao;
        this.goldInfoApi = goldInfoApi;
    }

    public void saveGoldData(GoldInfo goldInfo, String itmsNm) {
        // 금종목이 이미 존재하는지 확인
        int count = goldDao.checkGoldCategoryExists(Integer.parseInt(goldInfo.getSrtnCd()));

        // 금종목이 없다면 삽입
        if (count == 0) {
            goldDao.insertGoldCategory(Integer.parseInt(goldInfo.getSrtnCd()), itmsNm);  // 금종목 삽입
        }

        // 금 시세 데이터 삽입
        goldDao.insertGoldData(goldInfo);
    }


    // OpenAPI로부터 데이터를 가져와서 DB에 저장하는 메서드
    @Override
    @Scheduled(cron = "0 0 8 * * ?") // 매일 08시에 실행
    public void setDataFromAPI() throws ParseException {
        // 1. 테이블이 비었는지 확인
        int recordcount = goldDao.isTableEmpty();
        List<GoldInfo> goldInfoList = null; // 초기값을 null로 설정

        // 2. 테이블에 데이터가 존재하면
        if (recordcount > 0) {
            // 마지막으로 업데이트한 날짜 구하기
            String lastUpdateDate = goldDao.getLastBasDt();
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

            // 날짜 차이 계산
            int dayDiff = DateUtil.calcDateDiff(lastUpdateDate, today) - 1;

            // 최신일이 같은 날이면 0 반환하고 종료
            if (dayDiff <= 0) {
                log.info("GoldService setDataFromAPI : 최신데이터입니다.");
                return;
            }

            // 데이터가 없을 때까지 하루씩 이전으로 돌아가면서 확인
            boolean dataFound = false;
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(today)); // 오늘 날짜 설정

            while (dayDiff > 0 && !dataFound) {
                cal.add(Calendar.DATE, -1); // 하루씩 빼기
                String targetDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime()); // 날짜 포맷 변환
                try {
                    goldInfoList = JsonConverter.convertJsonToList(goldInfoApi.getGoldData("endBasDt", targetDate, 30));
                    if (!goldInfoList.isEmpty()) {
                        dataFound = true; // 데이터가 있으면 루프 종료
                    } else {
                        dayDiff--; // 데이터가 없으면 하루씩 이전으로 이동
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // 데이터가 찾아졌다면 저장
            if (dataFound) {
                // 맨 마지막으로 받아온 데이터가 최신 데이터이면 저장 안함
                if (goldInfoList.stream()
                        .anyMatch(goldInfo -> goldInfo.getBasDt() == Integer.parseInt(lastUpdateDate))) {
                    return;
                }
            } else {
                log.info("GoldService setDataFromAPI : 데이터가 없습니다.");
                return; // 데이터가 없을 경우 종료
            }

        } else {
            // 테이블이 비어 있을 경우 OpenAPI로부터 데이터 가져오기
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

            // 3년치 데이터 가져오기
            try {
                String response = goldInfoApi.getGoldData("endBasDt", today, 1095);
                goldInfoList = JsonConverter.convertJsonToList(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 데이터를 DB에 저장 (종목 확인 및 삽입 포함)
        if (goldInfoList != null) { // goldInfoList가 null이 아닐 때만 저장
            for (GoldInfo goldInfo : goldInfoList) {
                String itmsNm = "금";
                // 미니금이 아니라 금인 경우에만 저장
                if (goldInfo.getSrtnCd().equals("04020000")) {
                    itmsNm = "금";
                    saveGoldData(goldInfo, itmsNm);  // 금종목 확인 및 삽입
                } else if (goldInfo.getSrtnCd().equals("04020100")) {
                    itmsNm = "미니금";
                    saveGoldData(goldInfo, itmsNm);  // 금종목 확인 및 삽입
                }
            }
            log.info("GoldService setDataFromAPI : " + goldInfoList.size() + " row 삽입되었습니다.");
        }
    }

    // 현재 날짜로부터 주어진 기간(days)의 금 시세 데이터를 받아오는 함수
    @Override
    public List<GoldInfo> getGoldInfoInPeriod(String endBasDt, int days) throws ParseException {
        //스케줄링이 거의 불가능하므로 데이터 불러오기 default
        setDataFromAPI();
        // endBasDt부터 주어진 기간 동안의 데이터를 불러옴
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        String startBasDt = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        return goldDao.getGoldInfoInPeriod(startBasDt, endBasDt);
    }
}

package fi.re.firebackend.service.gold;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldRate;
import fi.re.firebackend.util.api.GoldInfoApi;
import fi.re.firebackend.util.api.JsonConverter;
import fi.re.firebackend.util.dateUtil.DateUtil;
import org.apache.log4j.Logger;
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

    private final int DAYS = 365 * 5;

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

    @Override
    @Scheduled(cron = "0 0 8 * * ?") // 매일 08시에 실행
    public void setDataFromAPI() throws ParseException {
        // 1. 테이블이 비었는지 확인
        int recordcount = goldDao.isTableEmpty();
        List<GoldInfo> goldInfoList = null;

        // 2. 테이블에 데이터가 존재하면
        if (recordcount > 0) {
            // 마지막으로 업데이트한 날짜 구하기
            String lastUpdateDate = goldDao.getLastBasDt();
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

            // 날짜 차이 계산
            int dayDiff = DateUtil.calcDateDiff(lastUpdateDate, today) - 1;

            if (dayDiff <= 0) {
                log.info("GoldService setDataFromAPI : 최신데이터입니다.");
                return;
            }

            // 데이터를 없을 때까지 하루씩 이전으로 돌아가면서 확인
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
                    log.error("GoldService setDataFromAPI: OpenAPI에서 데이터를 가져오는 중 예외가 발생했습니다. DB에서 최신 데이터를 반환합니다.", e);
                    return; // 예외 발생 시 저장된 가장 최신 데이터를 반환
                }
            }

            if (dataFound) {
                if (goldInfoList.stream()
                        .anyMatch(goldInfo -> goldInfo.getBasDt() == Integer.parseInt(lastUpdateDate))) {
                    return;
                }
            } else {
                log.info("GoldService setDataFromAPI : 데이터가 없습니다.");
                return;
            }

        } else {
            // 테이블이 비어 있을 경우 OpenAPI로부터 데이터 가져오기
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

            try {
                String response = goldInfoApi.getGoldData("endBasDt", today, DAYS);
                goldInfoList = JsonConverter.convertJsonToList(response);
            } catch (IOException e) {
                log.error("GoldService setDataFromAPI: OpenAPI에서 데이터를 가져오는 중 예외가 발생했습니다. DB에서 최신 데이터를 반환합니다.", e);
                return; // 예외 발생 시 저장된 가장 최신 데이터를 반환
            }
        }

        if (goldInfoList != null) {
            for (GoldInfo goldInfo : goldInfoList) {
                String itmsNm = "금";
                if (goldInfo.getSrtnCd().equals("04020000")) {
                    itmsNm = "금";
                    saveGoldData(goldInfo, itmsNm);
                } else if (goldInfo.getSrtnCd().equals("04020100")) {
                    itmsNm = "미니금";
                    saveGoldData(goldInfo, itmsNm);
                }
            }
            log.info("GoldService setDataFromAPI : " + goldInfoList.size() + " row 삽입되었습니다.");
        }
    }

    @Override
    public List<GoldInfo> getGoldInfoInPeriod(String endBasDt, int days) throws ParseException {
        try {
            setDataFromAPI();
        } catch (Exception e) {
            log.error("GoldService getGoldInfoInPeriod: API에서 데이터를 가져오는 중 예외 발생, 최신 DB 데이터를 반환합니다.", e);
        }

        // endBasDt부터 주어진 기간 동안의 데이터를 불러옴
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        String startBasDt = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        return goldDao.getGoldInfoInPeriod(startBasDt, endBasDt);
    }

    @Override
    public double rate() {
        GoldRate goldRate = goldDao.rate();
        double rate = (double) goldRate.getDayPrc() / goldRate.getClpr();
        return Math.round(rate * 100) / 100.0;
    }
}

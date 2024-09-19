package fi.re.firebackend.service.gold;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.util.api.GoldInfoApi;
import fi.re.firebackend.util.api.JsonConverter;
import fi.re.firebackend.util.dateUtil.DateUtil;
import fi.re.firebackend.util.goldPredict.GoldPriceExpectation;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final GoldDao goldDao;
    private final GoldInfoApi goldInfoApi;

    @Autowired
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
    public int setDataFromAPI() {
        // 1. 테이블이 비었는지 확인
        int recordcount = goldDao.isTableEmpty();
        List<GoldInfo> goldInfoList;

        // 2. 테이블에 데이터가 존재하면
        if (recordcount > 0) {
            // 마지막으로 업데이트한 날짜 구하기
            String lastUpdateDate = goldDao.getLastBasDt();
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String lastWeekDay = "";

            // today를 평일 날짜로 변환
            try {
                lastWeekDay = DateUtil.getMostRecentWeekday(today);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // 날짜 차이 계산
            int dayDiff = DateUtil.calcDateDiff(lastUpdateDate, lastWeekDay) - 1;

            // 최신일이 같은 날이면 0 반환하고 종료
            if (dayDiff <= 0) {
                System.out.println("최신 데이터입니다.");
                return 0;
            }

            // API 호출하여 새로운 데이터를 가져옴
            try {
                goldInfoList = JsonConverter.convertJsonToList(goldInfoApi.getGoldData("endBasDt", lastWeekDay, dayDiff));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //맨 마지막으로 받아온 데이터가 최신 데이터이면 저장 안함
            if (goldInfoList.stream()
                    .anyMatch(goldInfo -> goldInfo.getBasDt() == Integer.parseInt(lastUpdateDate))) {
                return 0;
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
        for (GoldInfo goldInfo : goldInfoList) {
            String itmsNm = "금";
            //미니금이 아니라 금인 경우에만 저장
            if (goldInfo.getSrtnCd().equals("4020000")) {
                itmsNm = "금";
                saveGoldData(goldInfo, itmsNm);  // 금종목 확인 및 삽입
            } else if (goldInfo.getSrtnCd().equals("4020100")) {
                itmsNm = "미니금";
                saveGoldData(goldInfo, itmsNm);  // 금종목 확인 및 삽입
            }
        }

        return goldInfoList.size();  // 삽입된 데이터의 개수 반환
    }

    // 현재 날짜로부터 주어진 기간(days)의 금 시세 데이터를 받아오는 함수
    @Override
    public List<GoldInfo> getGoldInfoInPeriod(String endBasDt, int days) {
        // endBasDt부터 주어진 기간 동안의 데이터를 불러옴
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        String startBasDt = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

        return goldDao.getGoldInfoInPeriod(startBasDt, endBasDt);
    }


}

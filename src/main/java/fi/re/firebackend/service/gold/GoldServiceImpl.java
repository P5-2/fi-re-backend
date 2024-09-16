package fi.re.firebackend.service.gold;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.util.api.GoldInfoApi;
import fi.re.firebackend.util.api.JsonConverter;
import fi.re.firebackend.util.dateUtil.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

            // last day, today 평일 날짜로 바꾸기
            try {
                lastWeekDay = DateUtil.getMostRecentWeekday(today);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // 최신일이 같은 날이면 0 반환
            if (lastUpdateDate.equals(lastWeekDay)) {
                System.out.println("최신 데이터입니다.");
                return 0;
            }

            // 날짜 차이 계산 후 API 호출
            int dayDiff = DateUtil.calcDateDiff(lastUpdateDate, lastWeekDay);
            try {
                goldInfoList = JsonConverter.convertJsonToList(goldInfoApi.getGoldData("endBasDt", lastWeekDay, dayDiff));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 불러온 데이터 DB에 저장 (종목 확인 및 삽입 포함)
            for (GoldInfo goldInfo : goldInfoList) {
                String itmsNm = "금"; // 미니금, 금
                saveGoldData(goldInfo, itmsNm);  // 금종목 확인 및 삽입
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

            // 데이터를 DB에 저장 (종목 확인 및 삽입 포함)
            for (GoldInfo goldInfo : goldInfoList) {
                String itmsNm = "금";  // 미니금, 금
                saveGoldData(goldInfo, itmsNm);  // 금종목 확인 및 삽입
            }
        }

        return goldInfoList.size();  // 삽입된 데이터의 개수 반환
    }


    // 최근 업데이트된 날짜와 현재 날짜 비교
//    @Override
//    public int getLastUpdateDate() {
//        String lastBasDt = goldDao.getLastBasDt();
//        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        if (lastBasDt == null) return -1;
//
//        // 날짜 차이 계산
//        int dayDiff = DateUtil.calcDateDiff(lastBasDt, today);
//
//        // 만약 오늘이 주말이면 날짜를 평일로 조정
//        Calendar cal = Calendar.getInstance();
//        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
//
//        if (dayOfWeek == Calendar.SUNDAY) {
//            dayDiff -= 2;  // 일요일 -> 금요일
//        } else if (dayOfWeek == Calendar.SATURDAY) {
//            dayDiff -= 1;  // 토요일 -> 금요일
//        }
//
//        return dayDiff;
//    }

    // 현재 날짜로부터 주어진 기간(days)의 금 시세 데이터를 받아오는 함수
//    @Override
//    public List<GoldInfo> getGoldInfoInPeriod(String endBasDt, int days) {
//        // endBasDt부터 주어진 기간 동안의 데이터를 불러옴
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -days);
//        String startBasDt = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
//
//        return goldDao.getGoldInfoInPeriod(startBasDt, endBasDt);
//    }

    // 예측된 금값 받아오는 함수
//    @Override
//    public List<GoldInfo> getFutureGoldPrice() throws Exception {
//        try {
//            String futureDate = "20240913";  // 예시로 예측하려는 날짜
//            List<GoldInfo> predictedGoldPrices = JsonConverter.convertJsonToList(getGoldData("basDt", futureDate));
//            return predictedGoldPrices;
//        } catch (Exception e) {
//            throw new Exception("Error fetching future gold price: " + e.getMessage());
//        }
//    }
}

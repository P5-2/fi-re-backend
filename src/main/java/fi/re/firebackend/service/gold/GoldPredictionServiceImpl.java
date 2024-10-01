package fi.re.firebackend.service.gold;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.util.goldPredict.GoldPriceExpectation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoldPredictionServiceImpl implements GoldPredictionService {

    private static final Logger log = Logger.getLogger(GoldPredictionServiceImpl.class);
    private final GoldService goldService;
    GoldPriceExpectation goldPriceExpectation;
    GoldDao goldDao;

    public GoldPredictionServiceImpl(GoldPriceExpectation goldPriceExpectation, GoldDao goldDao, GoldService goldService) {
        this.goldPriceExpectation = goldPriceExpectation;
        this.goldDao = goldDao;
        this.goldService = goldService;
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?") //초 분 시 일 월 요일 현재는 08시 정각
    public void goldPredictUpdate() throws Exception {
        deleteOldGoldPrices();
        saveGoldPredictData();
    }

    // 현재 저장된 예측 값과 예측된 결과의 차이를 구해서 차이 만큼의 미래만 저장
    public void saveGoldPredictData() throws Exception {
        try {
            String startDate = "20220627"; //DB에 저장된 가장 빠른 날짜 불러와서 사용해도 되지만 성능적인 면에서 fix 해놓음
            String endDate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            int isGoldCategoryEmpty = goldDao.isTableEmpty();
            if(isGoldCategoryEmpty == 0){
                goldService.setDataFromAPI();
            }

            //학습 모듈 호출
            List<GoldPredicted> predictedList = goldPriceExpectation.lstm(goldDao.getGoldInfoInPeriod(startDate, endDate));
            //저장된 예측 값이 얼마나 있는지 확인하고

            //Scheduled 메서드가 잘 동작한다면 predictedList size가 크거나 같을 것이므로
            String lastPredictedDate = goldDao.getLastPBasDt() == null ? "00000000" : goldDao.getLastPBasDt();
            System.out.println(lastPredictedDate);
            // 가장 큰 날짜 이후의 예측 데이터만 필터링
            List<GoldPredicted> filteredPredictions = predictedList.stream()
                    .filter(predicted -> predicted.getPBasDt().compareTo(lastPredictedDate) > 0)
                    .collect(Collectors.toList());
            //그 차이만큼의 예측치만 db에 저장
            for (GoldPredicted predicted : filteredPredictions) {
                // 해당 날짜가 이미 존재하는지 확인
                int count = goldDao.checkGoldCategoryExists(Integer.parseInt(predicted.getPBasDt()));
                // 날짜가 없다면 삽입
                if (count == 0) {
                    goldDao.insertGoldPredictData(predicted);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error fetching future gold price: " + e.getMessage());
        }


    }

    // 예측된 금값 받아오는 함수
    @Override
    public List<GoldPredicted> getFutureGoldPrice() {
        String today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String recentPBasDt = goldDao.getLastPBasDt();
        if(recentPBasDt == null || Integer.parseInt(recentPBasDt) < Integer.parseInt(today)){
            try {
                this.saveGoldPredictData();
            } catch (Exception e) {
                log.error(e.getMessage());
                return new ArrayList<>();
            }
        }
        //db에 저장된 날짜가 오늘 날짜보다 미래거나 같으면
        return goldDao.getGoldPredictData(today);
    }

    // 하루마다 예측했던 금 값 삭제하는 작업(오늘 날짜까지의 예측 데이터 삭제)
    // until today라고 생각하면 될 듯
    @Transactional
    public void deleteOldGoldPrices() {
        // 현재 날짜에 따라 금 시세 데이터 삭제
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

        int res = goldDao.deleteGoldPredictData(today);
        if (res > 0) {
            System.out.println("Deleting old gold prices before date: " + today);
        } else {
            System.out.println("fail to delete " + today);
        }

    }
}

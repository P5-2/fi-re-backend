package fi.re.firebackend.service.gold;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.util.goldPredict.GoldPriceExpectation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoldPredictionServiceImpl implements GoldPredictionService {

    GoldPriceExpectation goldPriceExpectation;
    GoldDao goldDao;

    public GoldPredictionServiceImpl(GoldPriceExpectation goldPriceExpectation, GoldDao goldDao) {
        this.goldPriceExpectation = goldPriceExpectation;
        this.goldDao = goldDao;
    }

    // 현재 저장된 예측 값과 예측된 결과의 차이를 구해서 차이 만큼의 미래만 저장(수정하기)
    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveGoldPredictData() throws Exception {
        try {
            String startDate = "20220627"; //DB에 저장된 가장 빠른 날짜 불러와서 사용해도 되지만 성능적인 면에서 fix 해놓음
            String endDate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

            //학습 모듈 호출
            List<GoldPredicted> predictedList = goldPriceExpectation.lstm(goldDao.getGoldInfoInPeriod(startDate, endDate));
            //저장된 예측 값이 얼마나 있는지 확인하고

            //Scheduled 메서드가 잘 동작한다면 predictedList size가 크거나 같을 것이므로
            String lastPredictedDate = goldDao.getLastPBasDt();

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
    public List<GoldPredicted> getFutureGoldPrice(String today) {
        return goldDao.getGoldPredictData(today);
    }

    // 하루마다 예측했던 금 값 삭제하는 작업(오늘 날짜까지의 예측 데이터 삭제)
    // until today라고 생각하면 될 듯
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Override
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

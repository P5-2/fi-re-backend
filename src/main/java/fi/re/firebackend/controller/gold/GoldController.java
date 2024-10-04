package fi.re.firebackend.controller.gold;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.service.gold.GoldPredictionService;
import fi.re.firebackend.service.gold.GoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/gold")
public class GoldController {

    private final GoldService goldService;
    private final GoldPredictionService goldPredictionService;

    @Autowired
    public GoldController(GoldService goldService, GoldPredictionService goldPredictionService) {
        this.goldService = goldService;
        this.goldPredictionService = goldPredictionService;
    }

    // 기준 날짜와 데이터 양을 기준으로 금 시세 데이터를 요청
    // /gold/info?endBasDt={현재 혹은 궁금한 날짜}&numOfRows={파라미터}
    @GetMapping("/info")
    public List<GoldInfo> getGold(
            @RequestParam("endBasDt") String endBasDt,
            @RequestParam("numOfRows") int numOfRows) throws ParseException {
        return goldService.getGoldInfoInPeriod(endBasDt, numOfRows);
    }

    // 예측된 금 시세 데이터 요청
    // /gold/predict
    @GetMapping("/predict")
    public List<GoldPredicted> getFutureGoldPrice() {
        try {
            // 미래 금 시세 데이터를 가져옴
            return goldPredictionService.getFutureGoldPrice();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 예측 데이터를 가져오는 중 에러가 발생한 경우 null 반환
        }
    }


    @GetMapping("/test")
    public List<GoldPredicted> test() {
        try {
            goldService.setDataFromAPI();
            goldPredictionService.goldPredictUpdate();
            return goldPredictionService.getFutureGoldPrice();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

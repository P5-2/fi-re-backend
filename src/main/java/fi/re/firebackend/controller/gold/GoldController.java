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
    @GetMapping("/info")
    public List<GoldInfo> getGold(@RequestParam("endBasDt") String endBasDt, @RequestParam("numOfRows") int numOfRows) throws ParseException {
        return goldService.getGoldInfoInPeriod(endBasDt, numOfRows);
    }

    // 예측된 금 시세 데이터 요청
    @GetMapping("/predict")
    public List<GoldPredicted> getFutureGoldPrice() {
        try {
            return goldPredictionService.getFutureGoldPrice();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    //6개월 뒤 금 이율 계산
    @GetMapping("/rate")
    public double rate() {
        return goldService.rate();
    }
}

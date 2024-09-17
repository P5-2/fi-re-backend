package fi.re.firebackend.controller.gold;

import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.service.gold.GoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gold")
public class GoldController {

    private final GoldService goldService;

    @Autowired
    public GoldController(GoldService goldService) {
        this.goldService = goldService;
    }


    //DB에 API 받아와서 저장하는 요청
    @GetMapping("/init")
    public ResponseEntity<Object> setDataFromAPI() {
        return ResponseEntity.ok(goldService.setDataFromAPI());
    }


    // 기준 날짜와 데이터 양을 기준으로 금 시세 데이터를 요청
    // /gold/info?endBasDt={현재 혹은 궁금한 날짜}&numOfRows={파라미터}
    @GetMapping("/info")
    public List<GoldInfo> getGold(
            @RequestParam("endBasDt") String endBasDt,
            @RequestParam("numOfRows") int numOfRows) {

        return goldService.getGoldInfoInPeriod(endBasDt, numOfRows);
    }

    // 기간 내 모든 금 시세 데이터를 호출 (최대 1년)
    // /gold/allData?endBasDt={종료 날짜}
    @GetMapping("/allData")
    public List<GoldInfo> getAllGoldDataInPeriod(@RequestParam("endBasDt") String endBasDt) {
        return goldService.getGoldInfoInPeriod(endBasDt, 365); // 최대 1년치 데이터 반환
    }


    // 예측된 금 시세 데이터 요청
    // /gold/predict/{period}
    @GetMapping("/predict")
    public List<GoldPredicted> getFutureGoldPrice() {
        try {
            // 미래 금 시세 데이터를 가져옴
            return goldService.getFutureGoldPrice();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 예측 데이터를 가져오는 중 에러가 발생한 경우 null 반환
        }
    }

//
//    // 하루 금 시세 요청 (basDt로 기준 날짜의 데이터를 요청)
//    // /gold/baseDate?basDt={기준 날짜}
//    @GetMapping("/baseDate")
//    public GoldInfo getBaseDateGoldPrice(@RequestParam("basDt") String basDt) {
//        List<GoldInfo> goldInfoList = goldService.getGoldInfoInPeriod(basDt, 1); //numOfRows 1로 고정(하루만 가져오기)
//        if (!goldInfoList.isEmpty()) {
//            return goldInfoList.get(0); // 하루 데이터이므로 첫 번째 값 반환
//        }
//        return null; // 데이터가 없을 경우 처리 추가하기
//    }
//


}
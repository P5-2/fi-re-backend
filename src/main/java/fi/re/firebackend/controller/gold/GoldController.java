package fi.re.firebackend.controller.gold;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.service.gold.GoldService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gold")
public class GoldController {
    private final GoldService goldService;

    public GoldController(GoldService goldService){
        this.goldService = goldService;
    }


    //하루 금 값 요청
    @GetMapping("/goldInfo/date")
    public GoldInfo getBaseDateGoldPrice(@RequestParam String basDt){

        return null;
    }

    //차트 및 표로 그려주기 위한 기간 내 모든 데이터 호출(최대 1년)
    @GetMapping("/goldInfo/endBasDate")
    public List<GoldInfo> getAllGoldDataInPeriod(@RequestParam String endBasDt){
        return null;
    }

    @GetMapping("/gold/goldPredict")
    public List<GoldInfo> getFutureGoldPrice(){
        //최대 3개월 정도 데이터를 한번에
        return null;
    }
}

package fi.re.firebackend.controller.forex;

import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.service.forex.ForexService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/forex")
public class ForexController {

    private final ForexService forexService;

    public ForexController(ForexService forexService) {
        this.forexService = forexService;
    }

    // API를 통해 특정 날짜의 외환 정보 검색
    @GetMapping("/date/{searchDate}")
    public ResponseEntity<List<ForexDto>> getExchangeRateByDate(@PathVariable String searchDate) {
        LocalDate date = LocalDate.parse(searchDate); // 문자열을 LocalDate로 변환
        List<ForexDto> rates = forexService.getExchangeRateByDate(date);
        //null이 반환될 수도 있는데 그건 프론트에서 할지 백엔드에서 처리해줄지 고민해보기
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/test")
    public ResponseEntity<String> getExchangeRateTest() {
        LocalDate today = LocalDate.now();
        try {
            forexService.setForexFromApi();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String res = forexService.getExchangeRateByDate(today).toString();
        return ResponseEntity.ok(res == null ? "null" : res);
    }
}
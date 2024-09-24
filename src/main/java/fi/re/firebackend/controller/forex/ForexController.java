package fi.re.firebackend.controller.forex;

import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.dto.forex.ForexResponseDto;
import fi.re.firebackend.service.forex.ForexService;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    public ResponseEntity<List<ForexResponseDto>> getExchangeRateByDate(@PathVariable String searchDate) {
        try {
            // 날짜 문자열을 형식에 맞춰 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(searchDate, formatter);
            System.out.println("date: " + date);

            // 해당 날짜의 환율을 가져옴
            List<ForexResponseDto> rates = forexService.getExchangeRateByDate(date);
            System.out.println("rates : "+rates);
            // 리스트가 비어있는 경우 처리
            if (rates.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }

            return ResponseEntity.ok(rates); // 200 OK
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/test")
    public ResponseEntity<String> getExchangeRateTest() throws IOException, ParseException {
        LocalDate today = LocalDate.now();
        try {
            forexService.setForexDataFromApi(today);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        String res = forexService.getExchangeRateByDate(today).toString();
        return ResponseEntity.ok(res == null ? "null" : res);
    }
}
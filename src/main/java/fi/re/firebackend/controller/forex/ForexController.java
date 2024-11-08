package fi.re.firebackend.controller.forex;

import fi.re.firebackend.dto.forex.ForexResponseDto;
import fi.re.firebackend.service.forex.ForexService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forex")
public class ForexController {

    private final ForexService forexService;

    // API를 통해 특정 날짜의 외환 정보 검색
    @GetMapping("/date/{searchDate}")
    public ResponseEntity<List<ForexResponseDto>> getExchangeRateByDate(@PathVariable String searchDate) {
        try {
            List<ForexResponseDto> rates = forexService.getExchangeRateForDate(searchDate);
            // 리스트가 비어있는 경우
            if (rates.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }

            return ResponseEntity.ok(rates); // 200 OK
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
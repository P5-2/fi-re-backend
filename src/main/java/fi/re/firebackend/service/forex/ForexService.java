package fi.re.firebackend.service.forex;

import fi.re.firebackend.dto.forex.ForexResponseDto;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public interface ForexService {
    void setForexFromApi() throws IOException, ParseException;

    List<ForexResponseDto> getExchangeRateByDate(LocalDate searchDate);
}

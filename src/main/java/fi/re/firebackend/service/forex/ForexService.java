package fi.re.firebackend.service.forex;

import fi.re.firebackend.dto.forex.ForexDto;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public interface ForexService {
    void setForexFromApi() throws IOException, ParseException;
    List<ForexDto> getExchangeRateByDate(LocalDate searchDate);
}

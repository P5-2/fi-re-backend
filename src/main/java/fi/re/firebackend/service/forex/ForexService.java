package fi.re.firebackend.service.forex;

import fi.re.firebackend.dto.forex.ForexResponseDto;
import fi.re.firebackend.dto.forex.ForexWrapper;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public interface ForexService {
    List<ForexResponseDto> getExchangeRateForDate(String searchDate) throws IOException, ParseException;

    ForexWrapper setForexDataFromApi(LocalDate date) throws IOException, ParseException;

    List<ForexResponseDto> getExchangeRateByDate(LocalDate searchDate) throws IOException, ParseException;
}

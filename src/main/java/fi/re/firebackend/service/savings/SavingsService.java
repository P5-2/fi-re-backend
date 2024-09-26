package fi.re.firebackend.service.savings;

import fi.re.firebackend.dao.finance.savings.SavingsDao;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sun.jvm.hotspot.debugger.Page;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SavingsService {
    private final RestTemplate restTemplate;
    private final SavingsDao savingsDao;
    private final String API_URL = "http://finlife.fss.or.kr/finlifeapi/savingProductsSearch.json";
    private final String API_KEY = "API_KEY";

    @Autowired
    public SavingsService(RestTemplate restTemplate, SavingsDao savingsDao) {
        this.restTemplate = restTemplate;
        this.savingsDao = savingsDao;
    }

}

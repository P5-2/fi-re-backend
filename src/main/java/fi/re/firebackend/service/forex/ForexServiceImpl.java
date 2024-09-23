package fi.re.firebackend.service.forex;

import fi.re.firebackend.dao.forex.ForexDao;
import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.util.api.ForexApi;
import fi.re.firebackend.util.api.ForexJsonConverter;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ForexServiceImpl implements ForexService {
    private static final Logger log = Logger.getLogger(ForexServiceImpl.class);
    private final ForexDao forexDao;
    private final ForexApi forexApi;

    public ForexServiceImpl(ForexDao forexDao, ForexApi forexApi) {
        this.forexDao = forexDao;
        this.forexApi = forexApi;
    }

    @Override
    @Scheduled(cron = "0 0 12 * * ?") //초 분 시 일 월 요일 현재는 12시 정각(영업일 11시 전후로 업데이트되므로)
    public void setForexFromApi() throws IOException {
//        String searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String searchDate = "20240920";
        String forexResult = forexApi.getForexData(searchDate);

        log.info("forex res: " + forexResult);
        if (forexResult != null && !forexResult.isEmpty()) {
            List<ForexDto> rates = ForexJsonConverter.convertJsonToList(forexResult);
            for (ForexDto rate : rates) {
                forexDao.insertExchangeRate(rate);
            }
        } else {
            log.info("No forex data found");
        }
    }

    // 특정 날짜의 외환 정보 검색
    public List<ForexDto> getExchangeRateByDate(LocalDate searchDate) {
        return forexDao.selectExchangeRateByDate(searchDate);
    }
}

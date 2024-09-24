package fi.re.firebackend.service.forex;

import fi.re.firebackend.dao.forex.ForexDao;
import fi.re.firebackend.dto.forex.ForexCategoryEntity;
import fi.re.firebackend.dto.forex.ForexDto;
import fi.re.firebackend.dto.forex.ForexEntity;
import fi.re.firebackend.dto.forex.ForexResponseDto;
import fi.re.firebackend.util.api.ForexApi;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 오늘 날짜로(12시 전이면 어제 날짜로 api에서 불러와서 저장하는 함수)
    @Scheduled(cron = "0 0 12 * * ?") //초 분 시 일 월 요일 현재는 12시 정각(영업일 11시 전후로 업데이트되므로)
    public void setForexFromApi() throws IOException, ParseException {
        LocalDate todayDate = isBeforeNoon(LocalDate.now());
        setForexDataFromApi(todayDate);
    }

    //api에서 매개변수로 받는 날짜를 불러오는 함수
    @Override
    public void setForexDataFromApi(LocalDate date) throws IOException, ParseException {

        // DateTimeFormatter를 사용하여 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String searchDate = date.format(formatter);
        // 오늘날짜로 api 가져오기
        List<ForexDto> forexResult = forexApi.getForexData(searchDate);

        log.info("forex res: " + forexResult);
        if (!forexResult.isEmpty()) {
            for (ForexDto forex : forexResult) {
                saveExchangeRate(forex, searchDate);
            }
        } else {
            log.info("No forex data found");
        }
    }

    //DB에 저장하는 함수(데이터가 있는지 확인하고 없으면 insert 있으면 update)
    public void saveExchangeRate(ForexDto forexDto, String searchDate) throws IOException, ParseException {
        // 1. cur_unit이 exchange_rate_category에 존재하는지 확인
        String curUnit = forexDto.getCurUnit();
        if (forexDao.isCurUnitExists(curUnit) == 0) {
            // 해당 cur_unit이 존재하지 않으면 새로운 카테고리 추가
            ForexCategoryEntity newCategory = new ForexCategoryEntity();
            newCategory.setCurUnit(curUnit);
            newCategory.setCurNm(forexDto.getCurNm()); // 필요한 경우 cur_nm 설정
            forexDao.insertExchangeRateCategory(newCategory);
        }

        // 2. 중복 체크 및 데이터 삽입 또는 업데이트
        LocalDate parsedDate = LocalDate.parse(searchDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        // ForexEntity 생성
        ForexEntity forexEntity = createForexEntity(forexDto, parsedDate, curUnit);
        Map<String, Object> dateUnitMap = new HashMap<>();
        dateUnitMap.put("searchDate", forexEntity.getSearchDate());
        dateUnitMap.put("curUnit", forexEntity.getCurUnit());

        int count = forexDao.isExistsBySearchDateAndCurUnit(dateUnitMap);
//        System.out.println(count);
        // 데이터가 존재하는지 확인 후 업데이트 또는 삽입
        if (count > 0) {
            forexDao.updateExchangeRate(forexEntity);
            System.out.println("updated forex");
        } else {
            forexDao.insertExchangeRate(forexEntity);
            System.out.println("insert exchange rate");
        }
    }

    @Override
    // 특정 날짜의 외환 정보 검색
    public List<ForexResponseDto> getExchangeRateByDate(LocalDate searchDate) throws IOException, ParseException {

        //searchDate가 오늘이면
        if (searchDate.equals(LocalDate.now())) {
            searchDate = isBeforeNoon(searchDate);
        }

        Map<String, Object> dateUnitMap = new HashMap<>();
        dateUnitMap.put("searchDate", searchDate);
        dateUnitMap.put("curUnit", "KRW"); //원화를 기준으로 데이터를 받았는지 확인
        // db에 없으면 api에서 받아오기
        if (forexDao.isExistsBySearchDateAndCurUnit(dateUnitMap) == 0) {
            setForexDataFromApi(searchDate);
        }

        return forexDao.selectExchangeRateByDate(searchDate);
    }

    private ForexEntity createForexEntity(ForexDto forexDto, LocalDate searchDate, String curUnit) {
        ForexEntity forexEntity = new ForexEntity();
        forexEntity.setSearchDate(searchDate);
        forexEntity.setCurUnit(curUnit);
        forexEntity.setResult(forexDto.getResult());
        forexEntity.setTtb(parseStringToDouble(forexDto.getTtb()));
        forexEntity.setTts(parseStringToDouble(forexDto.getTts()));
        forexEntity.setDealBasR(parseStringToDouble(forexDto.getDealBasR()));
        forexEntity.setBkpr(parseStringToInt(forexDto.getBkpr()));
        forexEntity.setYyEfeeR(parseStringToInt(forexDto.getYyEfeeR()));
        forexEntity.setTenDdEfeeR(parseStringToInt(forexDto.getTenDdEfeeR()));
        forexEntity.setKftcBkpr(parseStringToInt(forexDto.getKftcBkpr()));
        forexEntity.setKftcDealBasR(parseStringToDouble(forexDto.getKftcDealBasR()));
        return forexEntity;
    }

    public static double parseStringToDouble(String numberStr) {
        if (numberStr == null || numberStr.isEmpty()) {
            throw new NumberFormatException("Input string is null or empty");
        }
        // 콤마를 제거한 후 double로 변환
        return Double.parseDouble(numberStr.replace(",", ""));
    }

    public static int parseStringToInt(String numberStr) {
        if (numberStr == null || numberStr.isEmpty()) {
            throw new NumberFormatException("Input string is null or empty");
        }
        // 콤마를 제거하고 int로 변환
        return Integer.parseInt(numberStr.replace(",", ""));
    }

    public LocalDate isBeforeNoon(LocalDate date) {
        //현재 시간 구하기
        LocalTime today = LocalTime.now();
        if (today.isBefore(LocalTime.NOON)) {
            date = date.minusDays(1);
        }
        return date;
    }


}

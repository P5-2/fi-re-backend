package fi.re.firebackend.service.forex;

import fi.re.firebackend.dao.forex.ForexDao;
import fi.re.firebackend.dto.forex.*;
import fi.re.firebackend.util.api.ForexApi;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ForexServiceImpl implements ForexService {
    private static final Logger log = Logger.getLogger(ForexServiceImpl.class);
    private final ForexDao forexDao;
    private final ForexApi forexApi;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 오늘 날짜로(12시 전이면 어제 날짜로 api에서 불러와서 저장하는 함수)
    @Scheduled(cron = "0 0 12 * * ?", zone = "Asia/Seoul")    //월~금 초 분 시 일 월 요일 12시 정각에 업데이트(영업일 11시 전후로 업데이트되므로)
    public void setForexFromApi() throws IOException, ParseException {
        LocalDate todayDate = isBeforeNoon(LocalDate.now());
        setForexDataFromApi(todayDate);
    }

    @Override
    public List<ForexResponseDto> getExchangeRateForDate(String searchDate) throws IOException, ParseException {
        // 날짜 문자열을 형식에 맞춰 변환
        LocalDate date = LocalDate.parse(searchDate, formatter);
        log.info("date: " + date);

        // 최근 저장 날짜 확인
        LocalDate recentDate = forexDao.recentDate();
        if (recentDate != null && recentDate.isAfter(date)) {
            // 파라미터의 날짜가 DB 최신 날짜보다 이전이면 해당 날짜의 환율을 DB에서 가져옴
            return getExchangeRateByDate(date);
        } else {
            // API 호출 및 DB 저장 후 환율 정보를 반환
            ForexWrapper forexRes = setForexDataFromApi(date);

            if (forexRes == null) {
                log.info("forexRes is null, get "+forexDao.recentDate());
                return getExchangeRateByDate(forexDao.recentDate()); // 가장 최근 날짜의 환율 반환
            }
            return getExchangeRateByDate(forexRes.getSearchDate());
        }
    }



    //api에서 매개변수로 받는 날짜를 불러오는 함수
    @Override
    public ForexWrapper setForexDataFromApi(LocalDate date) throws IOException, ParseException {

        // DateTimeFormatter를 사용하여 포맷
        String searchDate = date.format(formatter);
        // 오늘날짜로 api 가져오기
        ForexWrapper forexResult = forexApi.getForexData(searchDate);
        List<ForexDto> forexDtoList = forexResult.getForexData();

        if (!forexDtoList.isEmpty()) {
            for (ForexDto forex : forexDtoList) {
                saveExchangeRate(forex, forexResult.getSearchDate().format(formatter));
            }
            return forexResult;
        } else {
            log.info("No forex data found");
            return null;
        }
    }

    //DB에 저장하는 함수(데이터가 있는지 확인하고 없으면 insert 있으면 update)
    public void saveExchangeRate(ForexDto forexDto, String searchDate) {
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
        LocalDate parsedDate = LocalDate.parse(searchDate, formatter);

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
        LocalDate recentDate = forexDao.recentDate();
        if(searchDate == null){
            searchDate = LocalDate.now(); //null이면 오늘 날짜 넣음(임시)
        }
        // 오늘 날짜와 같은 경우 오전인지 확인
        if (searchDate.equals(LocalDate.now())) {
            searchDate = isBeforeNoon(searchDate);
        }

        // 날짜와 기준 통화를 담은 맵 생성
        Map<String, Object> dateUnitMap = new HashMap<>();
        dateUnitMap.put("searchDate", searchDate);
        dateUnitMap.put("curUnit", "KRW"); // 원화를 기준으로 데이터를 받았는지 확인

        // DB에 해당 데이터가 존재하지 않으면 API에서 받아오기
        if (forexDao.isExistsBySearchDateAndCurUnit(dateUnitMap) == 0) {
            // API에서 데이터를 가져온 후 저장
            ForexWrapper forexRes = setForexDataFromApi(searchDate);
            if (forexRes == null || forexRes.getSearchDate() == null) {
                log.info("Failed to fetch data from API for date: " + searchDate);
                return Collections.emptyList(); // API에서 데이터가 없으면 빈 리스트 반환
            }

            List<ForexResponseDto> result = forexDao.selectExchangeRateByDate(forexRes.getSearchDate());
            log.info("New data fetched from API: " + result);
            return result.isEmpty() ? forexDao.selectExchangeRateByDate(recentDate) : result; // 데이터가 없으면 가장 최근 환율 반환
        }

        // DB에 데이터가 존재하는 경우
        List<ForexResponseDto> existingRates = forexDao.selectExchangeRateByDate(searchDate);
        log.info("Existing data found: " + existingRates);
        return existingRates;
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

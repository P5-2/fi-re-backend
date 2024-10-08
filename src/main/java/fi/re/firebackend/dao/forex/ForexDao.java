package fi.re.firebackend.dao.forex;

import fi.re.firebackend.dto.forex.ForexCategoryEntity;
import fi.re.firebackend.dto.forex.ForexEntity;
import fi.re.firebackend.dto.forex.ForexResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ForexDao {
    // 데이터 삽입
    void insertExchangeRate(ForexEntity forexEntity);

    // cur_unit이 존재하는지
    int isCurUnitExists(String curUnit);

    // exchange 카테고리 삽입
    void insertExchangeRateCategory(ForexCategoryEntity forexCategoryEntity);

    // 복합키(searchdate와 curunit)로 날짜에 저장된 unit이 있는지 확인
    int isExistsBySearchDateAndCurUnit(Map<String, Object> dateUnitMap);

    // 특정 날짜의 외환 정보를 검색
    List<ForexResponseDto> selectExchangeRateByDate(@Param("searchDate") LocalDate searchDate);

    // DB에 저장된 가장 최근 날짜
    LocalDate recentDate();

    int updateExchangeRate(ForexEntity forexEntity);
}

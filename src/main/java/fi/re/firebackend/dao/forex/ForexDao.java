package fi.re.firebackend.dao.forex;

import fi.re.firebackend.dto.forex.ForexDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Mapper
@Repository
public interface ForexDao {
    // 데이터 삽입
    void insertExchangeRate(ForexDto exchangeRate);

    // 특정 날짜의 외환 정보를 검색
    List<ForexDto> selectExchangeRateByDate(@Param("searchDate") LocalDate searchDate);
}

package fi.re.firebackend.dao.finance.savings;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface SavingsDao {
    //예적금 상세정보
    SavingsDto getSavingsById(int prdNo);

    //예적금 Hot리스트
    List<SavingsDto> hot();

    //에적금 전체 리스트
    List<SavingsDto> all();

    // type에 따른 목록 가져오기
    List<SavingsDto> selectSavingsByType(String type);
}

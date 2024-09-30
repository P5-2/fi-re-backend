package fi.re.firebackend.dao.recommendation;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import fi.re.firebackend.dto.recommendation.SavingsDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SavingsRcmdDao {
    //적금 상세정보
    SavingsDepositEntity getSavingsByCode(@Param("finPrdtCd") String finPrdtCd);

    //적금 Hot리스트
    List<SavingsDepositEntity> getHotSavings();

    //적금 전체 리스트
    List<SavingsDepositEntity> getAllSavings();

}


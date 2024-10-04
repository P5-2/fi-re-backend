package fi.re.firebackend.dao.recommendation;

import fi.re.firebackend.dto.recommendation.SavingsDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepositRcmdDao {
    //예금 상세정보
    SavingsDepositEntity getDepositByCode(@Param("finPrdtCd") String finPrdtCd);

    //예금 Hot리스트
    List<SavingsDepositEntity> getHotDeposit();

    //예금 전체 리스트
    List<SavingsDepositEntity> getAllDeposit();

}

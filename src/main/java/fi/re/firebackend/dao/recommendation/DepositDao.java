package fi.re.firebackend.dao.recommendation;

import fi.re.firebackend.dto.recommendation.DepositEntity;
import fi.re.firebackend.dto.recommendation.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepositDao {
    List<DepositEntity> getAllDeposit();
    MemberEntity getMemberInfo(String username);
}

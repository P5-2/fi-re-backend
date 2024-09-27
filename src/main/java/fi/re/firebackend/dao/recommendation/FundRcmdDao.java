package fi.re.firebackend.dao.recommendation;

import fi.re.firebackend.dto.recommendation.MemberEntity;
import fi.re.firebackend.dto.recommendation.filtering.FundVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundRcmdDao {
    List<FundVo> getAllFund();

    MemberEntity getMemberInfo(String username);
}

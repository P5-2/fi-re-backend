package fi.re.firebackend.dao.recommendation;

import fi.re.firebackend.dto.recommendation.FundEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundRcmdDao {
    List<FundEntity> getAllFund();
}

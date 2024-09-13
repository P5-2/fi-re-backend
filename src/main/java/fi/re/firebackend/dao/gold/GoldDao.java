package fi.re.firebackend.dao.gold;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GoldDao {
    String getLastBasDt();

}

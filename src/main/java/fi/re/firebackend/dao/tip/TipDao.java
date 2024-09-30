package fi.re.firebackend.dao.tip;
import fi.re.firebackend.dto.tip.TipDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TipDao {
    List<TipDto> findAll();
}

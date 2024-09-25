package fi.re.firebackend.dao.financeWord;

import fi.re.firebackend.dto.financeWord.FinanceWordDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FinanceWordDao {
    int count();
    int insertFinanceWord(FinanceWordDto financeWordDto);
    FinanceWordDto getFinanceWord(int id);
}

package fi.re.firebackend.dao.exp;

import fi.re.firebackend.dto.exp.ExpDto;
import fi.re.firebackend.dto.exp.VisitedDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;




@Mapper
@Repository
public interface ExpDao {
//  방문 날짜 확인하기
   String getDate(VisitedDto visitedDto);

//    날짜 갱신
    void updateDate(VisitedDto visitedDto);

    void insertVisited(VisitedDto visitedDto);

    void decreaseExp(String username);

    boolean existsVisited(VisitedDto visitedDto);

    void updateExp(ExpDto expDto);

    void allDelete(String username);
}

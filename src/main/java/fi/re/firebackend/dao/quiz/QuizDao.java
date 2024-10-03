package fi.re.firebackend.dao.quiz;

import fi.re.firebackend.dto.quiz.QuizDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QuizDao {
    QuizDto getQuizById(int id);
}

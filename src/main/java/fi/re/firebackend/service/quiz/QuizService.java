package fi.re.firebackend.service.quiz;

import fi.re.firebackend.dao.quiz.QuizDao;
import fi.re.firebackend.dto.quiz.QuizDto;
import org.springframework.stereotype.Service;

@Service
public class QuizService {
    QuizDao quizDao;
    QuizNumber quizNumber;

    public QuizService(QuizDao quizDao, QuizNumber quizNumber) {
        this.quizDao = quizDao;
        this.quizNumber = quizNumber; //생성하면서 오늘날짜의 랜덤 숫자 생성
    }

    public QuizDto getQuiz(String date) {
        int getNumber = quizNumber.getNumber(date);
        return quizDao.getQuizById(getNumber);
    }
}

package fi.re.firebackend.controller.quiz;

import fi.re.firebackend.dto.quiz.QuizDto;
import fi.re.firebackend.service.quiz.QuizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class QuizController {
    QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/get")
    public QuizDto getQuiz(String date){
        return quizService.getQuiz(date);
    }
}

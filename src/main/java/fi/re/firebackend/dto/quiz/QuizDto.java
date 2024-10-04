package fi.re.firebackend.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
    private int id;
    private String question;
    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private int answer;
}

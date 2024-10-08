package fi.re.firebackend.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDto {
    private int totalScore;
    private String age;
    private int assets;
    private List<String> keywords;
    private int salary;

}

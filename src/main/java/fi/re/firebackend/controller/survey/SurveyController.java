package fi.re.firebackend.controller.survey;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.dto.survey.SurveyDto;
import fi.re.firebackend.service.gold.GoldPredictionService;
import fi.re.firebackend.service.gold.GoldService;
import fi.re.firebackend.service.survey.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

//    의존성
    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/result")
    public ResponseEntity<String> resultSurvey(@RequestBody SurveyDto surveyDto) {
        System.out.println("Received survey data: {}" + surveyDto);
        surveyService.insertSurveyResult(surveyDto, "");
        return ResponseEntity.ok().body(surveyDto.toString());
    }

}

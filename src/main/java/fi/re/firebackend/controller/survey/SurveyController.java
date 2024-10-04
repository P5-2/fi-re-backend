package fi.re.firebackend.controller.survey;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.dto.survey.SurveyDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.service.gold.GoldPredictionService;
import fi.re.firebackend.service.gold.GoldService;
import fi.re.firebackend.service.survey.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/survey")
public class SurveyController {


    private final SurveyService surveyService;
    private final JwtTokenProvider jwtTokenProvider;



//    의존성
    @Autowired
    public SurveyController(SurveyService surveyService, JwtTokenProvider jwtTokenProvider) {
        this.surveyService = surveyService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/result")
    public ResponseEntity<String> resultSurvey(@RequestBody SurveyDto surveyDto, HttpServletRequest request) {
        // 헤더에서 토큰 가져오기 (Bearer 접두어 제거 및 공백 제거)
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰이 필요합니다.");
        }

        // JWT 토큰에서 사용자 정보 추출
        System.out.println("받아옴");
        String username = jwtTokenProvider.getUserInfo(token);
        surveyService.insertSurveyResult(surveyDto, username);
        System.out.println(surveyDto);
        return ResponseEntity.ok().body(surveyDto.toString());
    }

}

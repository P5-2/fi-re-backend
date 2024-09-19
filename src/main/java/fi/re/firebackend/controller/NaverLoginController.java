package fi.re.firebackend.controller;

import fi.re.firebackend.dto.login.MemberDto;
import fi.re.firebackend.service.login.NaverLoginService;
import org.nd4j.shade.jackson.databind.JsonNode;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/naver")
public class NaverLoginController {
    private final NaverLoginService service;

    public NaverLoginController(NaverLoginService service) {
        this.service = service;
    }

    // 콜백 해서 토큰 호출
    @GetMapping("/callback")
    public ResponseEntity<String> handleNaverCallback(@RequestParam String code, @RequestParam String state, HttpSession session) {
        String clientId = "wHXxK_xfYQJP4U42Ueey";  // 실제 Client ID로 교체
        String clientSecret = "qTHo_aIrhm";        // 실제 Client Secret으로 교체
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        // 토큰 요청을 위한 파라미터 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        // RestTemplate을 사용해 네이버에 토큰 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);
            System.out.println("NaverLoginController.handleNaverCallback >> ");
            return ResponseEntity.ok(response.getBody());  // 받은 토큰 정보를 그대로 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 요청 실패: " + e.getMessage());
        }
    }

    // 프로필 불러오기 + 프로필 정보 db 저장
    @GetMapping("/profile")
    public ResponseEntity<String> getNaverProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        // access_token을 사용해 네이버 API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String profileUrl = "https://openapi.naver.com/v1/nid/me";
        ResponseEntity<String> response = restTemplate.exchange(profileUrl, HttpMethod.GET, entity, String.class);

        try {
            // ObjectMapper를 사용하여 JSON 문자열을 Map으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            Map<String, Object> profile = (Map<String, Object>) responseBody.get("response");

            // Profile 정보를 MemberDto에 매핑
            MemberDto memberDto = new MemberDto();
            memberDto.setUsername((String) profile.get("email")); // 이메일
            memberDto.setPlatform("Naver");                       // 플랫폼 (고정값)
            memberDto.setNickname((String) profile.get("nickname"));

            // 나이 계산 (네이버는 나이를 범위로 제공하므로 임시로 25세로 설정)
            String ageRange = (String) profile.get("age");
            Integer age = ageRange != null ? Integer.parseInt(ageRange.split("-")[0]) : 25;
            memberDto.setAge(age);

            // 나머지 필드에 기본값 설정
            memberDto.setSalary(1000);
            memberDto.setAssets(1000);
            memberDto.setRiskPoint(1000);
            memberDto.setExp(1000);
            memberDto.setGoalAmount(1000);
            memberDto.setKeyword("default"); // 임시 키워드 설정
            System.out.println(memberDto);

            // 데이터베이스에 저장하는 서비스 호출
            service.addMember(memberDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing profile data");
        }

        return ResponseEntity.ok(response.getBody());
    }

    // 리프레시 토큰 추출 메서드
    private String extractRefreshToken(String tokenResponse) {
        try {
            // JSON 파싱을 위한 ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 JsonNode로 파싱
            JsonNode jsonNode = objectMapper.readTree(tokenResponse);
            // 리프레시 토큰을 추출
            return jsonNode.path("refresh_token").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String extractNameFromProfile(String profileResponse) {
        try {
            // ObjectMapper를 사용해 JSON을 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(profileResponse);

            // JSON에서 "response" 노드를 찾아 "name" 값을 가져옴
            JsonNode responseNode = rootNode.path("response");
            String name = responseNode.path("name").asText();  // "name" 필드 값 추출

            return name != null ? name : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractAccessToken(String tokenResponse) {
        try {
            // JSON 파싱을 위한 ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 JsonNode로 파싱
            JsonNode jsonNode = objectMapper.readTree(tokenResponse);
            // 액세스 토큰을 추출
            return jsonNode.path("access_token").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}


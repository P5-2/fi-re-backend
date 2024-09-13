package fi.re.firebackend.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/naver")
public class NaverLoginController {

    @GetMapping("/callback")
    public ResponseEntity<String> naverCallback(@RequestParam String code, @RequestParam String state) {
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
            return ResponseEntity.ok(response.getBody());  // 받은 토큰 정보를 그대로 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 요청 실패: " + e.getMessage());
        }
    }


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

        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("NaverLoginController test >> ");

        return "test";
    }
}


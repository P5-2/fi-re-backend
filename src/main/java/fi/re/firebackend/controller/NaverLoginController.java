package fi.re.firebackend.controller;

import fi.re.firebackend.dto.login.NaverTokenDto;
import fi.re.firebackend.dto.login.TokenResponseDto;
import fi.re.firebackend.jwt.dto.TokenDto;
import fi.re.firebackend.service.login.NaverLoginService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/naver")
public class NaverLoginController {
    private final NaverLoginService service;

    public NaverLoginController(NaverLoginService service) {
        this.service = service;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> naverCallback(@RequestParam("code") String code, @RequestParam("state") String state) throws IOException {

        NaverTokenDto naverTokenDto = service.getNaverToken(code, state);
        TokenDto tokenDto = service.loginWithNaver(naverTokenDto);

        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .roles(tokenDto.getGrantType())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDto);
    }
    /*// 콜백 해서 토큰 호출
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
    }*/

    // 프로필 불러오기 + 프로필 정보 db 저장



}


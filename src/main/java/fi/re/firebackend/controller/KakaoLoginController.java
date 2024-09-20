package fi.re.firebackend.controller;

import fi.re.firebackend.dto.login.KakaoTokenDto;
import fi.re.firebackend.dto.login.TokenResponseDto;
import fi.re.firebackend.jwt.dto.TokenDto;
import fi.re.firebackend.service.login.KakaoLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/kakao")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    public KakaoLoginController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/oauth2/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) throws IOException {

        KakaoTokenDto kakaoTokenDto = kakaoLoginService.getKakaoToken(code);
        TokenDto tokenDto = kakaoLoginService.loginWithKakao(kakaoTokenDto);

        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .roles(tokenDto.getGrantType())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDto);
    }

}

package fi.re.firebackend.controller.login;

import fi.re.firebackend.dto.login.KakaoTokenDto;
import fi.re.firebackend.dto.login.TokenResponseDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.jwt.dto.TokenDto;
import fi.re.firebackend.service.login.KakaoLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/kakao")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    private final UserDetailsService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginController(KakaoLoginService kakaoLoginService, UserDetailsService userService, JwtTokenProvider jwtTokenProvider) {
        this.kakaoLoginService = kakaoLoginService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/callback")
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

    @GetMapping("/findname")
    public ResponseEntity<?> findName(HttpServletRequest request) {
        System.out.println("findName");

        // 헤더에서 토큰 가져오기 (Bearer 접두어 제거 및 공백 제거)
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        // JWT 토큰에서 사용자 정보 추출
        String username = jwtTokenProvider.getUserInfo(token);

        // 사용자 정보 가져오기
        UserDetails userDetails = userService.loadUserByUsername(username);

        // 사용자 닉네임 가져오기
        String nickName = kakaoLoginService.findName(username);

        // 닉네임 반환
        return ResponseEntity.status(HttpStatus.OK).body(nickName);
    }



}

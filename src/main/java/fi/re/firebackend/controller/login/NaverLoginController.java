package fi.re.firebackend.controller.login;

import fi.re.firebackend.dto.login.NaverTokenDto;
import fi.re.firebackend.dto.login.TokenResponseDto;
import fi.re.firebackend.dto.login.UserResponseDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.jwt.dto.TokenDto;
import fi.re.firebackend.service.login.NaverLoginService;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/naver")
public class NaverLoginController {
    private final NaverLoginService service;
    private final UserDetailsService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public NaverLoginController(NaverLoginService service, UserDetailsService userService, JwtTokenProvider jwtTokenProvider) {
        this.service = service;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
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

    @GetMapping("/finduser")
    public ResponseEntity<?> findName(HttpServletRequest request) {
        System.out.println("finduser");

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
        String nickName = service.findName(username);

        // DTO 생성 후 반환
        UserResponseDto userResponseDTO = new UserResponseDto(username, nickName);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }
}

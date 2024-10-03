package fi.re.firebackend.jwt;

import fi.re.firebackend.jwt.dto.RefreshToken;
import fi.re.firebackend.jwt.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.*;
import javax.annotation.PostConstruct;

// 토큰을 발행하고 받은 토큰을 분석하는 클래스
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    public static String httpHeaderKey = "Authorization"; // 허가
    private String securityKey = "myJWTkeymyJWTkeymyJWTkeymyJWTkeymyJWTkey";
    private long accessTokenValidTime = Duration.ofDays(4).toMillis(); // 액세스 토큰 유효시간 4일
    private long refreshTokenValidTime = Duration.ofDays(14).toMillis(); // 리프레시 토큰 유효시간 2주

    private Set<String> tokenBlacklist = new HashSet<String>(); // 블랙리스트

    private final UserDetailsService userService;

    // 비밀키를 Base64로 인코딩
    @PostConstruct
    protected void securityKeyEncoding(){
        securityKey = Base64.getEncoder().encodeToString(securityKey.getBytes());
    }

    // JWT 토큰 생성
    public TokenDto createToken(String userPk, List<String> roles) {
        System.out.println("createToken - 토큰 생성");
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, securityKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, securityKey) // same key for simplicity; consider using a different key
                .compact();

        System.out.println("생성된 accessToken ===> " + accessToken);
        System.out.println("생성된 refreshToken ===> " + refreshToken);
        return TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).key(userPk).build();
    }



    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUserInfo(token));
        System.out.println("username: " + userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(securityKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 회원 정보 추출
    public String getUserInfo(String token) {
        return Jwts.parser().setSigningKey(securityKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 리프레시 토큰의 유효성 및 재생성
    public String validateRefreshToken(RefreshToken refreshTokenObj) {
        String refreshToken = refreshTokenObj.getRefreshToken();

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(securityKey).parseClaimsJws(refreshToken);
            if (!claims.getBody().getExpiration().before(new Date())) {
                return recreateAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("roles"));
            }
        } catch (Exception e) {
            return null; // 리프레시 토큰 만료
        }
        return null;
    }

    // 액세스 토큰 재생성
    private String recreateAccessToken(String userPk, Object roles) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, securityKey)
                .compact();
    }
}


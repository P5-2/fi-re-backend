package fi.re.firebackend.security;

import lombok.RequiredArgsConstructor;
import fi.re.firebackend.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

//@Import(WebConfig.class)
//@ComponentScan(basePackageClasses = WebConfig.class)
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages  = {"fi.re.firebackend"})
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;        // 토큰 검사용

    // 회원가입 시에 암호화할 클래스
    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1.권한 filter
        http.csrf(csrf -> csrf.disable())  // csrf 설정을 off 합니다
                .addFilterBefore(this.corsFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .antMatchers("/naver/**").permitAll()
                        .antMatchers("/kakao/**").permitAll()
                        .anyRequest().authenticated());   // (무조건)증명

        // 2. 세션을 사용하지 않음
        http.sessionManagement((sessionManagement)
                ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) );

        // 3. JWT 필터를 사용여부 설정
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 4. login form



        // enable h2-console
//        http.headers(headers ->
//                headers.frameOptions(options ->
//                        options.sameOrigin()
//                )
//        );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 쿠키나 인증 정보 허용
        //config.addAllowedOrigin("http://localhost:5173"); // 허용할 출처
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용 (GET, POST, PUT, DELETE, etc.)

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}













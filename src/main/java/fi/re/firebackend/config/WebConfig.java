package fi.re.firebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "fi.re.firebackend")  // <- 공통 팩키지
public class WebConfig implements WebMvcConfigurer {

//    @Bean   // <- object 생성
//    public InternalResourceViewResolver viewResolver() {
//        System.out.println("WebConfig viewResolver() ~~~");
//
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//
//        viewResolver.setPrefix("/WEB-INF/views/");
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("WebConfigurer addCorsMappings ~");
        // 접속 클라이언트를 허가(Restful)
        registry.addMapping("/**").allowedOrigins("*");
        //registry.addMapping("/**").allowedOrigins("http://localhost:3000");
    }
/*
    @Bean
    public CorsFilter corsFilter() {
        System.out.println("^^ WebConfigurer corsFilter ~");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 쿠키나 인증 정보 허용
        //config.addAllowedOrigin("http://localhost:5173"); // 허용할 출처
        config.addAllowedOrigin("*");
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용 (GET, POST, PUT, DELETE, etc.)

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }*/

}

















package fi.re.firebackend.config;

//import mul.cam.e.security.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.Date;

@EnableWebMvc
@ComponentScan(basePackages = "fi.re.firebackend.")  // <- 공통 팩키지
public class WebConfig implements WebMvcConfigurer {

    /*@Bean   // <- object 생성
    public InternalResourceViewResolver viewResolver() {
        System.out.println("WebConfig viewResolver() ~~~");

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }*/

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


}

















package com.example.oauthjwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Mvc 컨트롤러에 대한 cors 설정
 *
 * 주로 비보안(인증이 필요 없는) 요청에 대해 적용
 * 보안 필터 체인을 통과하지 않는 요청이나 Spring Security가 관여하지 않는 경로에 대해 적용
 */
@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**") // 모든 경로
                .exposedHeaders("Set-Cookie") // 노출할 쿠키 헤더
                .allowedOrigins("http://localhost:3000"); // 웹 앱이 동작할 서버 주소
    }
}
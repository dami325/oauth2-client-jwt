package com.example.oauthjwt.security.config;

import com.example.oauthjwt.security.jwt.JWTFilter;
import com.example.oauthjwt.security.jwt.JWTUtil;
import com.example.oauthjwt.security.oauth2.CustomSuccessHandler;
import com.example.oauthjwt.security.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService; // 커스텀 서비스
    private final CustomSuccessHandler customSuccessHandler; // 인증 성공 커스텀
    private final JWTUtil jwtUtil; // JWT 파싱 및 발급

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Security 필터에대한 cors 설정
        // 컨트롤러에 대한 cors 설정은 CorsMvcConfig 클래스에 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        //허용할 주소
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));

                        // get, post .. 모두 허용
                        configuration.setAllowedMethods(Collections.singletonList("*"));

                        // credential값 가져올 수 있음
                        configuration.setAllowCredentials(true);

                        // 허용 헤더
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        // setExposedHeaders 애플리케이션 서버에서 클라이언트에게 주는것에 대한 설정
                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/login","/join","/logout","swagger-ui/**","/v3/api-docs/**","/error/**","/error-page/**").permitAll()
                        .requestMatchers("/admin").hasAuthority("ADMIN")
                        .anyRequest().authenticated());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //oauth2 커스텀 로그인 서비스 등록, 핸들러 등록,
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .loginPage("/"))// 커스텀 로그인 페이지 지정
                .logout(logout -> logout.logoutUrl("/logout"));

        //세션 설정 : STATELESS -> 요청한번이 끝나면 소멸됨
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

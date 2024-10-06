package com.example.oauthjwt.security.oauth2;

import com.example.oauthjwt.security.dto.CustomOAuth2User;
import com.example.oauthjwt.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * OAuth2 인증 성공시 처리할 핸들러
 *
 * SecurityConfig http.oauth2Login() 메서드에 설정 해줘야함
 *
 * CustomOAuth2UserService 클래스의 loadUser 에서 반환한 CustomOAuth2User가 principal
 *
 * 클라이언트에선 하이퍼링크로 OAuth2 로그인 화면만 표시해주고
 * 인증 과정을 모두 백에서 관리하는게 보안상 맞기 때문에(카카오 데브 글 등)
 * 쿠키를 사용한 JWT 발급을 구현 해야함
 *
 */
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        // 유저의 role은 하나라 가정
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, 60*60*60L);

        // 쿠키를 넣어 리다이렉트
        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true); //https 에서만 쿠키를 사용할 수 있는 설정
        cookie.setPath("/");
        cookie.setHttpOnly(true); // 자바스크립트로 쿠키에 접근 불가 true

        return cookie;
    }
}
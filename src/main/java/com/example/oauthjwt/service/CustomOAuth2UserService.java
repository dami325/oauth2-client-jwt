package com.example.oauthjwt.service;

import com.example.oauthjwt.dto.*;
import com.example.oauthjwt.entity.UserEntity;
import com.example.oauthjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 커스텀 구현 서비스를 사용하려면
 * SecurityConfig의 http.oauth2Login() 메서드에 등록 해줘야함
 *
 * OAuth2 로그인 형식에 맞게 유저를 반환하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("oAuth2User : {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Custom oAuth2Custom = null;
        if (registrationId.equals("naver")) {

            oAuth2Custom = new NaverCustom(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Custom = new GoogleCustom(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 애플리케이션에서 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Custom.getProvider()+" "+ oAuth2Custom.getProviderId();

        UserEntity existData = userRepository.findByUsername(username);


        if (existData == null) {
            // 처음 접속하는 사용자일 경우
            UserEntity userEntity = UserEntity.builder()
                    .username(username)
                    .email(oAuth2Custom.getEmail())
                    .name(oAuth2Custom.getName())
                    .role("ROLE_USER")
                    .build();

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Custom.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);

        }
        else {

            existData.changeEmail(oAuth2Custom.getEmail());
            existData.changeName(oAuth2Custom.getName());

            userRepository.save(existData);

            // role과 username은 바꾸지 않음
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Custom.getName());
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2User(userDTO);
        }

    }
}

package com.chung.oauth.demo.service;

import com.chung.oauth.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chung.oauth.demo.entity.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    // oauth2로 로그인 한 직후 유저 정보를 가져옴
    // oauth로부터 가져온 유저 정보를 db에도 추가하기 위함
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String,Object> attributes = oAuth2User.getAttributes();
        validateAttributes(attributes);
        registerIfNewUser(attributes, provider);
        return oAuth2User;
    }

    // 유저에 이메일이 있는 지 확인
    private void validateAttributes(Map<String, Object> attributes) {
        if (!attributes.containsKey("email")) {
            throw new IllegalArgumentException("Email not exist in OAuthUser");
        }
    }

    // 새로운 유저라면 db에 유저 추가
    private void registerIfNewUser(Map<String, Object> attributes, String provider) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        log.info("Login user: email: {}, name: {}, provider: {}", email, name, provider);
        // 존재하지 않는 유저라면 DB에 유저 추가
        User prevUser = userRepository.findByEmailAndProvider(email, provider);
        if (prevUser == null) {
            User newUser = User.builder().email(email).name(name).provider(provider).build();
            userRepository.save(newUser);
        }
    }
}

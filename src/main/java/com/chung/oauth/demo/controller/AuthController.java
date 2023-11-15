package com.chung.oauth.demo.controller;

import com.chung.oauth.demo.dto.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    // 유저 정보 가져오기
    @GetMapping("/user")
    public UserInfo getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }
        String userName = principal.getAttribute("name");
        return UserInfo.builder().name(userName).build();
    }
}

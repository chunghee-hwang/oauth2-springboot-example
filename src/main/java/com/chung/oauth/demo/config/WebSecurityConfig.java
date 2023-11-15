package com.chung.oauth.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Slf4j
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 방어 코드.
                // 로그인 후, 클라이언트 쿠키에 XSRF-TOKEN이 발급된다.
                // POST, PUT, DELETE 요청마다 헤더에 X-XSRF-TOKEN을 쿠키로부터 받아와서 추가한뒤 보내야 403에러가 뜨지 않는다.
                .csrf(c -> c
                                .csrfTokenRequestHandler((request, response, csrfToken) -> {
                                    CsrfToken token = csrfToken.get();
                                    String headerName = token.getHeaderName();
                                    String tokenValue = token.getToken();
                                    // csrfTokenRequestHandler를 추가해야지만 token을 쿠키로 내려주는 것 확인
//                            log.info("csrf Token Requested. headerName: {}, token: {}", headerName, tokenValue);
                                })
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                // 인증을 하지 않고 요청 가능한 uri 등록
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/index.html"),
                                new AntPathRequestMatcher("/error"),
                                new AntPathRequestMatcher("/webjars/**"),
                                new AntPathRequestMatcher("/logout")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 예외 발생시 로그 출력
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        log.error("Access denied: {}", accessDeniedException.getMessage()))
                )
                .oauth2Login(oauth2 -> oauth2.failureHandler((request, response, exception) -> {
                    // 로그인 실패 시 세션에 에러메시지 추가
                    request.getSession().setAttribute("error.message", exception.getMessage());
                }))
                // 로그인 성공시 /로 이동
                .logout(l -> l.logoutSuccessUrl("/").logoutSuccessHandler((request, response, authentication) -> {
                    // 로그인 성공 시 유저 이름 출력
                    Object principal = authentication.getPrincipal();
                    if (principal == null) {
                        log.info("Unknown user logout");
                    } else if (principal instanceof OAuth2User user) {
                        String userName = user.getAttribute("name");
                        log.info("User logout: {}", userName);
                    }
                }))
                .build();
    }
}

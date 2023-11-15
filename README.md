# Spring Boot 3.x + OAuth2 예제

[Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2) 안내글을 보고 작성한 예시 코드입니다.  
GitHub 또는 Google로 로그인 한 뒤, DB에 OAuth2에서 가져온 유저 정보를 저장하는 간단한 예시입니다.  
DB: h2를 사용하여 추가 설정없이 동작하도록 구성하였습니다.
## References
- https://spring.io/guides/tutorials/spring-boot-oauth2/#github-register-application
- https://yelimkim98.tistory.com/49
- https://velog.io/@swchoi0329/스프링-시큐리티와-OAuth-2.0으로-로그인-기능-구현


## 사전 설정

application.yml을 아래와 같이 설정합니다.  
단, spring.securit.oauth2.client.registration에서 clientId, clientSecret은 본인의 것으로 설정해야합니다.  

- GitHub
  - 설정 방법:
    - GitHub의 OAuth 2.0 인증 시스템을 로그인에 사용하려면 먼저 [새 GitHub 앱을 추가](https://github.com/settings/developers)해야 합니다.
    - "New OAuth App"을 선택한 후 "Register a new OAuth application" 페이지가 나타납니다.
    - 앱 이름과 설명을 입력합니다.
    - 그런 다음 앱의 홈 페이지를 입력합니다(http://localhost:8080).
    - 마지막으로 Authorization callback URL을 다음과 같이 입력합니다.
      - http://localhost:8080/login/oauth2/code/github
    - Application(애플리케이션 등록)을 클릭합니다.
    - OAuth 리디렉션 URI는 최종 사용자의 사용자 에이전트가 GitHub을 사용하여 인증하고 Authorize 응용 프로그램 페이지의 응용 프로그램에 대한 액세스 권한을 부여한 후 다시 리디렉션되는 응용 프로그램의 경로입니다.
- Google
  - 설정 방법
    - 이 [링크](https://velog.io/@swchoi0329/스프링-시큐리티와-OAuth-2.0으로-로그인-기능-구현)에 접속한 후 "2. 구글 서비스 등록" 항목을 보고 설정합니다.
    - 설정 과정에서 "승인된 리디렉션 URI"을 아래와 같이 입력합니다.
      - http://localhost:8080/login/oauth2/code/google

```yml

spring:
  security:
    oauth2:
      client:
        registration:
          github:
            clientId: ?
            clientSecret: ?
          google:
            client-id: ?
            clientSecret: ?
            scope: profile,email

  datasource:
    url: jdbc:h2:mem:mybatis-test
    driverClassName: org.h2.Driver
    username: sa
    hikari:
      maximum-pool-size: 4
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true

```

## 실행
- WAS를 실행하고, http://localhost:8080으로 접속합니다.
- GitHub 또는 Google로 로그인하면 클라이언트에 유저 네임이 정상적으로 출력되는 지 확인합니다.
- 서버 로그를 확인합니다.
  - 로그인 시 로그
  ```
  2023-11-16T01:42:24.680+09:00  INFO 7104 --- [nio-8080-exec-5] c.c.oauth.demo.service.OauthUserService  : Login user: email: ???@gmail.com, name: UserName, provider: google
  Hibernate:
    select
      u1_0.user_id,
      u1_0.email,
      u1_0.name,
      u1_0.provider
    from
      managed_users u1_0
    where
      u1_0.email=?
      and u1_0.provider=?
  Hibernate:
    insert
    into
    managed_users
    (email,name,provider,user_id)
    values
    (?,?,?,default)
  ```
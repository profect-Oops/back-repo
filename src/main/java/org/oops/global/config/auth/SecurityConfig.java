package org.oops.global.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()
                )
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        //.requestMatchers().hasRole(Role.USER.name())
                        .requestMatchers("/", "/login","/login.html", "/css/**", "images/**", "/static/js/**", "/logout/*", "/api/coin/**", "/api/news/**").permitAll()  //인증없어도 접근 가능
                        .requestMatchers("/api/alert/**").authenticated()  //인증해야만 접속 가능
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 세션 유지
                .logout( // 로그아웃 성공 시 / 주소로 이동
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/coin/list.html")
                )
                // OAuth2 로그인 기능에 대한 여러 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/coin/list.html", false)  // false로 하면 기존 요청을 유지하면서 리다이렉트하지 않아서 세션 유지됨
                        .failureHandler((request, response, exception) -> {
                            log.error("로그인 실패! 이유: {}", exception.getMessage());
                            response.sendRedirect("/coin/list.html");
                        })
                        .successHandler((request, response, authentication) -> {
                            // 로그인 성공 후 세션에 이메일 저장
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");

                            // 로그인 성공 로그
                            log.info("로그인 성공! 이메일: {}", email);

                            request.getSession().setAttribute("email", email);

                            // /coin/list.html 페이지로 리다이렉트
                            response.sendRedirect("/coin/list.html");
                        })
                );
        return http.build();
    }

}

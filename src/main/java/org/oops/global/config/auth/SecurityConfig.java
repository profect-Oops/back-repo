package org.oops.global.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2AuthorizedClientService authorizedClientService) throws Exception {
        log.info("Google Client ID: {}", System.getenv("GOOGLE_CLIENT_ID"));
        log.info("Google Client Secret: {}", System.getenv("GOOGLE_CLIENT_SECRET"));
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // iframe 허용
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        //.requestMatchers().hasRole(Role.USER.name())
                        .requestMatchers("/", "/login", "/static/login.html", "/css/**", "images/**", "/static/js/**", "/static/js/**", "/logout/*", "/api/coin/**", "/api/coin/add", "/api/coin/details","/api/news/**", "/static/index.html", "/static/coin/coinDetail.html").permitAll()  //인증없어도 접근 가능
                        .requestMatchers("/ws/**", "/ws","/api/coin/details/**", "/static/coin/coinDetail.html/**").permitAll() // WebSocket 및 API 허용
                        .requestMatchers("/api/**","/oauth2/**", "/login/oauth2/**").permitAll()  //추가!
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 세션 유지
                .logout(logoutConfig -> logoutConfig
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                        .clearAuthentication(true) // 인증 정보 초기화
                        .logoutSuccessUrl("/static/index.html") // 로그아웃 후 index.html로 이동
                        .permitAll()
                )
                // OAuth2 로그인 기능에 대한 여러 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/static/alert/alarm.html", false)  // false로 하면 기존 요청을 유지하면서 리다이렉트하지 않아서 세션 유지됨
                        .failureHandler((request, response, exception) -> {
                            log.info("Google Client ID: {}", System.getenv("GOOGLE_CLIENT_ID"));
                            log.info("Google Client Secret: {}", System.getenv("GOOGLE_CLIENT_SECRET"));
                            log.error("로그인 실패! 이유: {}", exception.getMessage());
                            response.sendRedirect("/static/index.html");
                        })
                        .successHandler((request, response, authentication) -> {
                            // 로그인 성공 후 세션에 이메일 저장
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");

                            //토큰 발급
//                            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("google", authentication.getName());
//                            if (authorizedClient != null) {
//                                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
//                                log.info("OAuth2 Access Token 저장됨: {}", accessToken.getTokenValue());
//
//                                // 세션에 Access Token 저장 가능
//                                request.getSession().setAttribute("accessToken", accessToken.getTokenValue());
//                            } else {
//                                log.warn("⚠️ OAuth2 Access Token을 찾을 수 없음!");
//                            }


                            // 로그인 성공 로그
                            log.info("로그인 성공! 이메일: {}", email);
                            log.info("Google Client ID: {}", System.getenv("GOOGLE_CLIENT_ID"));
                            log.info("Google Client Secret: {}", System.getenv("GOOGLE_CLIENT_SECRET"));
                            request.getSession().setAttribute("email", email);

                            // /index.html 페이지로 리다이렉트
                            response.sendRedirect("/static/alert/alarm.html");
                        })
                );
        return http.build();
    }

    // CORS 설정을 WebSecurity에 적용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "http://localhost:8080",
                "https://d3pdkkr961vb7.cloudfront.net",
                "https://todaycoinfo.com",
                "https://api.todaycoinfo.com"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/oauth2/**", configuration);  // OAuth2 CORS 허용
        source.registerCorsConfiguration("/login/oauth2/**", configuration);  // OAuth2 로그인 리디렉션 허용
        return source;
    }

}
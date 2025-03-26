package org.oops.global.config.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oops.api.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
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
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 에러 방지
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/coin/**", "/api/news/**","/api/redis/**", "/ws/**","/cache").permitAll()
                        .requestMatchers("/login","/login/oauth2/**", "/oauth2/**","/api/user/**","/api/auth/check").permitAll()
                        .requestMatchers("/","/index.html", "/static/index.html", "/coin/coinDetail.html","/static/coin/coinDetail.html").permitAll()
                        .requestMatchers("/api/alert/**","/alert/alarm.html","/static/alert/alarm.html").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(logoutConfig -> logoutConfig
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID", "auth") // JSESSIONID 쿠키 삭제
                        .clearAuthentication(true) // 인증 정보 초기화
                        .logoutSuccessUrl("/index.html") // 로그아웃 후 index.html로 이동
                        .permitAll()
                )
                // OAuth2 로그인 기능에 대한 여러 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth ->
                                auth.authorizationRequestResolver(customAuthorizationRequestResolver(clientRegistrationRepository))
                        )
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        //.defaultSuccessUrl("/static/alert/alarm.html", false)  // false로 하면 기존 요청을 유지하면서 리다이렉트하지 않아서 세션 유지됨
                        .defaultSuccessUrl("https://todaycoinfo.com/alert/alarm.html", true)  // false로 하면 기존 요청을 유지하면서 리다이렉트하지 않아서 세션 유지됨
                        .failureHandler((request, response, exception) -> {
                            log.info("Google Client ID: {}", System.getenv("GOOGLE_CLIENT_ID"));
                            log.info("Google Client Secret: {}", System.getenv("GOOGLE_CLIENT_SECRET"));

                            log.error("OAuth2 로그인 실패! 이유: {}", exception.getMessage(), exception); // 전체 스택 트레이스 출력
                            log.error("요청된 redirect_uri: {}", request.getParameter("redirect_uri"));
                            log.error("요청된 Authorization Code: {}", request.getParameter("code"));
                            log.error("요청된 Scope: {}", request.getParameter("scope"));
                            log.error("요청된 State: {}", request.getParameter("state"));

                            if (exception instanceof OAuth2AuthenticationException authException) {
                                String errorCode = authException.getError().getErrorCode();
                                String errorDescription = authException.getError().getDescription(); // <-- 여기!

                                log.error("OAuth2 ErrorCode: {}", errorCode);
                                log.error("OAuth2 ErrorDescription: {}", errorDescription);

                                // 클라이언트에 JSON 응답으로 내려주고 싶다면 아래처럼 작성
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write(
                                        String.format("{\"errorCode\":\"%s\",\"error\":\"%s\"}",
                                                errorCode,
                                                errorDescription != null ? errorDescription : "No error description from provider")
                                );
                            } else {
                                response.sendRedirect("https://todaycoinfo.com/alert/alarm.html");
                            }

                            //response.sendRedirect("/index.html");
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
//                            log.info("Google Client ID: {}", System.getenv("GOOGLE_CLIENT_ID"));
//                            log.info("Google Client Secret: {}", System.getenv("GOOGLE_CLIENT_SECRET"));
//                            request.getSession().setAttribute("email", email);

                            // CloudFront Function이 참조할 쿠키 설정
                            jakarta.servlet.http.Cookie loginCookie = new jakarta.servlet.http.Cookie("auth", "true");
                            loginCookie.setHttpOnly(true); // JS 접근 금지
                            loginCookie.setSecure(true);   // HTTPS 전용
                            loginCookie.setPath("/");
                            loginCookie.setMaxAge(60 * 60); // 1시간 유지
                            response.addCookie(loginCookie);


                            // /alert/alarm.html 페이지로 리다이렉트
                            response.sendRedirect("https://todaycoinfo.com/alert/alarm.html");
                        })
                );
        return http.build();
    }

    // CORS 설정을 WebSecurity에 적용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://todaycoinfo.com",
                "https://api.todaycoinfo.com",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization")); // 클라이언트에서 사용 가능하도록 추가

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request) {
                OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request);
                if (originalRequest == null) return null;

                return OAuth2AuthorizationRequest.from(originalRequest)
                        .redirectUri("https://todaycoinfo.com/login/oauth2/code/google")
                        .build();
            }

            @Override
            public OAuth2AuthorizationRequest resolve(jakarta.servlet.http.HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request, clientRegistrationId);
                if (originalRequest == null) return null;

                return OAuth2AuthorizationRequest.from(originalRequest)
                        .redirectUri("https://todaycoinfo.com/login/oauth2/code/" + clientRegistrationId)
                        .build();
            }
        };
    }
}
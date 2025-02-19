package org.oops.api.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.oops.global.config.auth.dto.SessionUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class LoginController {
    private final HttpSession httpSession;

    public LoginController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @GetMapping("/")
    public String index(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        System.out.println("컨트롤러 세션 확인: " + (user != null ? user.getEmail() : "세션 없음"));
        if (user != null) {
            model.addAttribute("email", user.getEmail());
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //로그아웃 기능은 지원하지 않음
//    //일반 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(HttpSession session, HttpServletResponse response) {
//        // 세션에서 사용자 정보 삭제
//        session.removeAttribute("user");
//        session.removeAttribute("email");
//        session.removeAttribute("SPRING_SECURITY_CONTEXT");
//
//        // Spring Security 인증 정보 삭제
//        SecurityContextHolder.clearContext();
//
//        session.invalidate();
//
//        // JSESSIONID 쿠키 삭제 (세션 무효화)
//        response.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Max-Age=0; SameSite=None; Secure");
//
//        // 캐시 방지 (로그아웃 후 새로고침하면 캐시된 세션이 없어지도록 설정)
//        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setHeader("Expires", "0");
//
//        try {
//            response.sendRedirect("/index.html"); // 로그아웃 후 index.html로 리다이렉트
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.ok().build();
//    }
//
//    // 소셜 로그아웃 (OAuth2 제공자 로그아웃 URL로 리다이렉트)
//    @GetMapping("/oauth/logout")
//    public void socialLogout(HttpSession session, HttpServletResponse response) throws IOException {
//        session.removeAttribute("email");
//        session.removeAttribute("SPRING_SECURITY_CONTEXT");
//        session.invalidate();
//        SecurityContextHolder.clearContext();
//        response.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Max-Age=0; SameSite=None; Secure");
//
//
//        String logoutUrl = "/index.html"; // 기본 로그아웃 후 이동할 URL
//
//        // 🚀 Google OAuth2 로그아웃 URL
//        logoutUrl = "https://accounts.google.com/logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:8080/login.html";
//
//
//        response.sendRedirect(logoutUrl); // 소셜 로그아웃 URL로 이동
//    }
}

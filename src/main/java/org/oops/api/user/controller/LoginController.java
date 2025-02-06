package org.oops.api.user.controller;

import jakarta.servlet.http.HttpSession;
import org.oops.global.config.auth.dto.SessionUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class LoginController {
    private final HttpSession httpSession;

    public LoginController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @GetMapping("/")
    public String index(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("email", user.getEmail());
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

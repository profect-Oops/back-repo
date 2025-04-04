package org.oops.api.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.oops.api.user.dto.UserDTO;
import org.oops.api.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping(value = "/find/all", produces = "application/json; charset=UTF-8")
    public List<UserDTO> findAll() {
        return userServiceImpl.findAll();
    }

    @GetMapping(value = "/find/{id}", produces = "application/json; charset=UTF-8")
    public UserDTO findById(@PathVariable Long id) {
        return userServiceImpl.findById(id);
    }

    @PutMapping(value = "/update/{id}", produces = "application/json; charset=UTF-8")
    public UserDTO update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userServiceImpl.update(id, userDTO);
    }

    @GetMapping("/email")
    public String getUserEmail(HttpSession session) {
        Object email = session.getAttribute("email");
        return email != null ? email.toString() : "";
    }

    @GetMapping("/check/login")
    public ResponseEntity<String> getEmailOrRedirect(HttpSession session) {
        Object email = session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(""); // 401 Unauthorized 반환
        }
        return ResponseEntity.ok(email.toString());
    }
}

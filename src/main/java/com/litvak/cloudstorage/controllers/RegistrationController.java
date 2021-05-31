package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/reg")
public class RegistrationController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registrationPage() {
        return "page_views/registration";
    }

    @PostMapping
    public String registration(Model model, @Param("login") String login,
                               @Param("password") String password,
                               @Param("matchingPassword") String matchingPassword) {
        login = login.trim();
        password = password.trim();
        matchingPassword = matchingPassword.trim();
        if(password.isEmpty() || matchingPassword.isEmpty() || login.isEmpty()) {
            model.addAttribute("empty", true);
            return "page_views/registration";
        }
        if(!Objects.equals(password, matchingPassword)) {
            model.addAttribute("notequals", true);
            return "page_views/registration";
        }

        password = passwordEncoder.encode(password);
        try {
            userService.createNewUser(login, password);
            model.addAttribute("success", true);
            return "page_views/login";
        } catch (Exception e) {
            model.addAttribute("error", true);
            return "page_views/registration";
        }
    }
}

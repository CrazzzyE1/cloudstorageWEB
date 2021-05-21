package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reg")
public class RegistrationController {
    AppService appService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public String registrationPage(Model model,
                                   @RequestParam(name = "error", required = false) String error ) {
        model.addAttribute("error", error);
        return "page_views/registration";
    }

    @PostMapping
    public String registration(Model model, @Param("login") String login,
                               @Param("password") String password) {
        password = passwordEncoder.encode(password);
        try{
            appService.createNewUser(login, password);
            model.addAttribute("success", "success");
            return "page_views/login";
        } catch (Exception e) {
            model.addAttribute("error", "error");
            return "page_views/registration";
        }
    }
}

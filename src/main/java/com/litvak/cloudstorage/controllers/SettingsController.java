package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;


@Controller
@RequestMapping("/settings")
public class SettingsController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showSettingsPage() {
        return "page_views/settings";
    }

    @PostMapping("/delete")
    public String removeAccount(Principal principal,
                                Model model,
                                @RequestParam(name = "password", required = false) String password) {
        if (password.trim().isEmpty()) return "redirect:/settings";
        User user = userService.getUserByUsername(principal.getName());
        if (passwordEncoder.matches(password, user.getPassword())) {
            userService.removeAccount(user);
            return "redirect:/logout";
        }
        model.addAttribute("password", true);
        return "page_views/settings";
    }

    @PostMapping("/change")
    public String changePassword(Model model,
                                 Principal principal,
                                 @RequestParam(name = "oldpassword") String oldPass,
                                 @RequestParam(name = "newpassword") String newPass) {
        if (oldPass.trim().isEmpty() || newPass.trim().isEmpty()) return "redirect:/settings";
        User user = userService.getUserByUsername(principal.getName());
        if (passwordEncoder.matches(oldPass, user.getPassword())) {
            userService.changePassword(passwordEncoder.encode(newPass), user);
            return "redirect:/main";
        }
        model.addAttribute("password", true);
        return "page_views/settings";
    }
}

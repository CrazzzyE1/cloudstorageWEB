package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;


@Controller
@RequestMapping("/settings")
public class SettingsController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showSettingsPage() {
        return "page_views/settings";
    }

    // TODO: 16.05.2021 Fix провека старого пароля
    @PostMapping("/delete")
    public String removeAccount(
            Principal principal,
            @RequestParam(name = "password", required = false) String password) {
        userService.removeAccount(principal);
        return "redirect:/logout";
    }

    // TODO: 16.05.2021 FIX Bug change password
    @PostMapping("/change")
    public String changePassword(
            Principal principal,
            @RequestParam(name = "oldpassword") String oldPass,
            @RequestParam(name = "newpassword") String newPass) {
            userService.changePassword(oldPass, newPass, principal);
        return "redirect:/main";
    }
}

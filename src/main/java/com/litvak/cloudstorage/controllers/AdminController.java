package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/admins")
public class AdminController {
    UserService userService;
    FileAppService fileAppService;
    DirAppService dirAppService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public void setFileAppService(FileAppService fileAppService) {
        this.fileAppService = fileAppService;
    }

    @Autowired
    public void setDirAppService(DirAppService dirAppService) {
        this.dirAppService = dirAppService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("admins", userService.getAllActiveAdmins());
        model.addAttribute("removedadmins", userService.getAllRemovedAdmins());
        model.addAttribute("removedusers", userService.getAllRemovedUsers());
        return "page_views/admins";
    }

    @GetMapping("/delete")
    public String deleteUser(Model model,
                             @RequestParam(name = "id") Long id) {
        userService.removeAccount(userService.getUserById(id));
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("admins", userService.getAllActiveAdmins());
        model.addAttribute("removedadmins", userService.getAllRemovedAdmins());
        model.addAttribute("removedusers", userService.getAllRemovedUsers());
        return "page_views/admins";
    }

    @GetMapping("/activate")
    public String activateUser(Model model,
                               @RequestParam(name = "id") Long id) {
        userService.activateAccount(userService.getUserById(id));
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("admins", userService.getAllActiveAdmins());
        model.addAttribute("removedadmins", userService.getAllRemovedAdmins());
        model.addAttribute("removedusers", userService.getAllRemovedUsers());
        return "page_views/admins";
    }

    @GetMapping("/edit")
    public String editUser(Model model,
                           @RequestParam(name = "id") Long id) {
        model.addAttribute("user", userService.getUserById(id));
        return "page_views/editForm";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam(name = "id") Long id,
                       @RequestParam(name = "password", required = false) String password,
                       @RequestParam(name = "space", required = false) Long space) {
        User user = userService.getUserById(id);
        if (!password.trim().isEmpty() && !passwordEncoder.matches(password, user.getPassword())) {
            userService.changePassword(passwordEncoder.encode(password), user);
        }
        if (space != null && space > 0 && space < 22000) {
            userService.changeSpace(space * 1024 * 1024, user);
        }
        return "redirect:/admins";
    }

    @GetMapping("/up")
    public String userToAdmin(@RequestParam(name = "id") Long id) {
        userService.upUser(id);
        return "redirect:/admins";
    }

    @GetMapping("/down")
    public String adminToUser(@RequestParam(name = "id") Long id) {
        userService.downAdmin(id);
        return "redirect:/admins";
    }

    @GetMapping("/reg")
    public String regUser() {
        return "page_views/regForm";
    }

    @PostMapping("/reg")
    public String regNewUser(Model model,
                             @Param("login") String login,
                             @Param("password") String password,
                             @Param("matchingPassword") String matchingPassword) {
        login = login.trim();
        password = password.trim();
        matchingPassword = matchingPassword.trim();
        if (password.isEmpty() || matchingPassword.isEmpty() || login.isEmpty()) {
            model.addAttribute("emptyy", true);
            return "page_views/regForm";
        }
        if (!Objects.equals(password, matchingPassword)) {
            model.addAttribute("notequals", true);
            return "page_views/regForm";
        }
        password = passwordEncoder.encode(password);
        try {
            userService.createNewUser(login, password);
            model.addAttribute("users", userService.getAllActiveUsers());
            model.addAttribute("admins", userService.getAllActiveAdmins());
            model.addAttribute("removedadmins", userService.getAllRemovedAdmins());
            model.addAttribute("removedusers", userService.getAllRemovedUsers());
            return "page_views/admins";
        } catch (Exception e) {
            model.addAttribute("error", true);
            return "page_views/regForm";
        }
    }
}

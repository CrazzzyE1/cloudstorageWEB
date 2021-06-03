package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.Role;
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

import java.util.List;
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
        List<User> users = userService.getAllActiveUsers();
        List<User> admins = userService.getAllActiveAdmins();
        List<User> removedUsers = userService.getAllRemovedUsers();
        List<User> removedadmins = userService.getAllRemovedAdmins();
        model.addAttribute("users", users);
        model.addAttribute("admins", admins);
        model.addAttribute("removedadmins", removedadmins);
        model.addAttribute("removedusers", removedUsers);
        return "page_views/admins";
    }

    @GetMapping("/delete")
    public String deleteUser(Model model,
                             @RequestParam(name = "id") Long id) {
        User user = userService.getUserById(id);
        userService.removeAccount(user);
        List<User> users = userService.getAllActiveUsers();
        List<User> admins = userService.getAllActiveAdmins();
        List<User> removedUsers = userService.getAllRemovedUsers();
        List<User> removedadmins = userService.getAllRemovedAdmins();
        model.addAttribute("users", users);
        model.addAttribute("admins", admins);
        model.addAttribute("removedadmins", removedadmins);
        model.addAttribute("removedusers", removedUsers);
        return "page_views/admins";
    }

    @GetMapping("/activate")
    public String activateUser(Model model,
                               @RequestParam(name = "id") Long id) {
        User user = userService.getUserById(id);
        userService.activateAccount(user);
        List<User> users = userService.getAllActiveUsers();
        List<User> admins = userService.getAllActiveAdmins();
        List<User> removedadmins = userService.getAllRemovedAdmins();
        List<User> removedUsers = userService.getAllRemovedUsers();
        model.addAttribute("users", users);
        model.addAttribute("admins", admins);
        model.addAttribute("removedadmins", removedadmins);
        model.addAttribute("removedusers", removedUsers);
        return "page_views/admins";
    }

    @GetMapping("/edit")
    public String editUser(Model model,
                           @RequestParam(name = "id") Long id) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "page_views/editForm";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam(name = "id") Long id,
                       @RequestParam(name = "password") String password,
                       @RequestParam(name = "space") Long space) {
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
    public String regNewUser(Model model, @Param("login") String login,
                             @Param("password") String password,
                             @Param("matchingPassword") String matchingPassword) {
        login = login.trim();
        password = password.trim();
        matchingPassword = matchingPassword.trim();
        if(password.isEmpty() || matchingPassword.isEmpty() || login.isEmpty()) {
            model.addAttribute("emptyy", true);
            return "page_views/regForm";
        }
        if(!Objects.equals(password, matchingPassword)) {
            model.addAttribute("notequals", true);
            return "page_views/regForm";
        }

        password = passwordEncoder.encode(password);
        try {
            userService.createNewUser(login, password);
            List<User> users = userService.getAllActiveUsers();
            List<User> admins = userService.getAllActiveAdmins();
            List<User> removedUsers = userService.getAllRemovedUsers();
            List<User> removedadmins = userService.getAllRemovedAdmins();
            model.addAttribute("users", users);
            model.addAttribute("admins", admins);
            model.addAttribute("removedadmins", removedadmins);
            model.addAttribute("removedusers", removedUsers);
            return "page_views/admins";
        } catch (Exception e) {
            model.addAttribute("error", true);
            return "page_views/regForm";
        }
    }
}

package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "page_views/admins";
    }

    @GetMapping("/delete")
    public String deleteUser(Model model,
                             @RequestParam(name = "id") Long id) {
        User user = userService.getUserById(id);
        userService.removeAccount(user);
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "page_views/admins";
    }

    @GetMapping("/activate")
    public String activateUser(Model model,
                               @RequestParam(name = "id") Long id) {
        User user = userService.getUserById(id);
        userService.activateAccount(user);
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
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
        if (space > 0 && space < 22000) {
            userService.changeSpace(space * 1024 * 1024, user);
        }
        return "redirect:/admins";
    }

    @GetMapping("/files")
    public String showFiles(Model model,
                            Principal principal,
                            @RequestParam(name = "login", required = false) String login) {
        if (login == null) login = principal.getName();
        Utilities.clearLinks(login);
        DirApp dirRoot = dirAppService.getRootDir(login);
        List<FileApp> files = dirRoot.getFiles();
        Long id = dirRoot.getId();
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(id));
        List<DirApp> links = Utilities.getLinks(login, dirRoot);
        String cutOrCopy = Utilities.showCutOrCopy(login);
        List<User> users = userService.getAllUsers();
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("links", links);
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("login", login);
        model.addAttribute("users", users);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        return "page_views/files";
    }
}

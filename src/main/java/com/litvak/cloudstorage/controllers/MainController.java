package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/main")
public class MainController {
    private FileAppService fileAppService;
    private DirAppService dirAppService;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDirAppService(DirAppService dirAppService) {
        this.dirAppService = dirAppService;
    }

    @Autowired
    public void setFileAppService(FileAppService fileAppService) {
        this.fileAppService = fileAppService;
    }

    @GetMapping()
    public String mainPage(Model model,
                           Principal principal,
                           @RequestParam(name = "login", required = false) String login) {

        if (login == null) {
            if (Utilities.getSelect(principal.getName()) != null) {
                login = Utilities.getSelect(principal.getName());
            } else {
                login = principal.getName();
            }
        }
        Utilities.saveSelect(principal.getName(), login);
        Utilities.clearLinks(login);
        DirApp dirRoot = dirAppService.getRootDir(login);
        Long id = dirRoot.getId();
        String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
        List<FileApp> files = dirRoot.getFiles();
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(id));
        String cutOrCopy = Utilities.showCutOrCopy(login);
        List<User> users = userService.getAllUsers();
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("role", role);
        model.addAttribute("login", login);
        model.addAttribute("users", users);
        return "page_views/main";
    }

    @GetMapping("/{id}")
    public String changeDirectory(Principal principal,
                                  @PathVariable(value = "id") Long id,
                                  @RequestParam(name = "login", required = false) String login,
                                  Model model) {
        if (login == null) {
            if (Utilities.getSelect(principal.getName()) != null) {
                login = Utilities.getSelect(principal.getName());
            } else {
                login = principal.getName();
            }
        }
        Utilities.saveSelect(principal.getName(), login);
        DirApp dir = dirAppService.getDirById(id);
        List<FileApp> files = dir.getFiles();
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(id));
        List<DirApp> links = Utilities.getLinks(login, dir);
        String cutOrCopy = Utilities.showCutOrCopy(login);
        String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
        List<User> users = userService.getAllUsers();
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("current_dir", dir);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("links", links);
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("login", login);
        model.addAttribute("role", role);
        model.addAttribute("users", users);
        return "page_views/main";
    }

    @GetMapping("/new")
    public String createNewDir(@RequestParam(value = "name") String name,
                               @RequestParam(value = "parent_id") Integer parent_id,
                               @RequestParam(value = "id") Integer id) {
        if (name.trim().isEmpty()) return "redirect:".concat(parent_id.toString());
        name = name.trim();
        dirAppService.createNewDir(name, parent_id, id);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/delete")
    public String deleteDir(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "parent_id") Integer parent_id) {
        dirAppService.removeDir(id);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/deletef")
    public String deleteFile(Principal principal,
                             @RequestParam(value = "id") Long id,
                             @RequestParam(value = "parent_id") Integer parent_id) {
        String login = Utilities.getSelect(principal.getName());
        DirApp dirTo = dirAppService.getRootDir(login.concat("_recycle"));
        fileAppService.moveFile(id, dirTo);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "search") String filename,
                         @RequestParam(value = "login") String login,
                         Principal principal) {
        DirApp dir = dirAppService.getRootDir(login);
        List<FileApp> files = fileAppService.getFilesByParams(dir.getUser().getId(), filename);
        String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
        List<User> users = userService.getAllUsers();
        model.addAttribute("files", files);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("current_dir", dir);
        model.addAttribute("role", role);
        model.addAttribute("login", login);
        model.addAttribute("users", users);
        return "page_views/main";
    }

    @GetMapping("/rename")
    public String rename(@RequestParam(value = "name") String name,
                         @RequestParam(value = "id") Long id,
                         @RequestParam(value = "current_dir") Integer current_dir) {
        if (!name.isEmpty()) {
            DirApp dir = dirAppService.getDirById(id);
            if (!Utilities.checkingFolderNameForDuplication(name, dirAppService.getDirsByDirParentId(current_dir))) {
                dir.setName(name);
                dirAppService.saveDir(dir);
            }
        }
        return "redirect:/main/".concat(current_dir.toString());
    }

    @GetMapping("/renamef")
    public String renamef(@RequestParam(value = "name") String name,
                          @RequestParam(value = "id") Long id,
                          @RequestParam(value = "current_dir") Integer current_dir) {

        if (!name.isEmpty() && !Utilities.checkingFileNameForDuplication(name,
                fileAppService.getAllFilesByDir(dirAppService.getDirById(current_dir.longValue())))) {
            FileApp file = fileAppService.getFileById(id).get();
            file.setName(name);
            fileAppService.saveFile(file);
        }
        return "redirect:/main/".concat(current_dir.toString());
    }
}

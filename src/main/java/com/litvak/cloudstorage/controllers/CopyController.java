package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/copy")
public class CopyController {

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

    @GetMapping("/copy{id}")
    public String copyFile(@PathVariable(value = "id") Long id,
                           @RequestParam(name = "login") String login,
                           @RequestParam(name = "current_page") Long current) {
        Utilities.saveCopyFileId(login, id);
        return "redirect:/main/".concat(current.toString());
    }

    @GetMapping("/cut{id}")
    public String cutFile(@PathVariable(value = "id") Long id,
                          @RequestParam(name = "login") String login,
                          @RequestParam(name = "current_page") Long current) {
        Utilities.saveCutFileId(login, id);
        return "redirect:/main/".concat(current.toString());
    }

    @GetMapping("/paste")
    public String paste(Model model,
                        Principal principal,
                        @RequestParam(value = "copy") String copy,
                        @RequestParam(name = "login") String login,
                        @RequestParam(name = "current_dir_id") Long current) {

        if(userService.getStorage(login) - fileAppService.getFilesSpace(login)
                - fileAppService.getFileById(Utilities.getFileId(login)).get().getSize() <= 0 && copy.equals("copy")) {
            Utilities.clearCopyStatus(login);
            return "redirect:/main/".concat(String.valueOf(current));
        }
        boolean flag = false;
        if (copy.equals("cut")) {
            flag = fileAppService.cutPasteFile(login, current);
        }
        if (copy.equals("copy")) {
            flag = fileAppService.copyPasteFile(login, current);
        }
        if (flag) {
            Utilities.clearCopyStatus(login);
            return "redirect:/main/".concat(current.toString());
        }
        DirApp dir = dirAppService.getDirById(current);
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("current_dir", dir);
        model.addAttribute("directories", dirAppService.getDirsByDirParentId(Math.toIntExact(current)));
        model.addAttribute("files", dir.getFiles());
        model.addAttribute("links", Utilities.getLinks(login, dir));
        model.addAttribute("copy", Utilities.showCutOrCopy(login));
        model.addAttribute("duplicate", true);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("login", login);
        model.addAttribute("role", userService.getUserByUsername(principal.getName()).getRoles()
                .stream().findFirst().get().getName());
        return "page_views/main";
    }

    @GetMapping("/replacement")
    public String paste(@RequestParam(value = "copy") String copy,
                        @RequestParam(name = "login") String login,
                        @RequestParam(name = "current_dir_id") Long current) {
        fileAppService.replacement(login, copy, current);
        Utilities.clearCopyStatus(login);
        return "redirect:/main/".concat(current.toString());
    }
}


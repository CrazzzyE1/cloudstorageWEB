package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.AppService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/copy")
public class CopyController {

    AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/copy{id}")
    public String copyFile(Principal principal,
                           @PathVariable(value = "id") Long id,
                           @RequestParam(name = "current_page") Long current) {
        Utilities.saveCopyFileId(principal.getName(), id);
        return "redirect:/main/".concat(current.toString());
    }

    @GetMapping("/cut{id}")
    public String cutFile(Principal principal,
                          @PathVariable(value = "id") Long id,
                          @RequestParam(name = "current_page") Long current) {
        Utilities.saveCutFileId(principal.getName(), id);
        return "redirect:/main/".concat(current.toString());
    }

    @GetMapping("/paste")
    public String paste(Principal principal,
                        @RequestParam(value = "copy") String copy,
                        @RequestParam(name = "current_dir_id") Long current) {
        String login = principal.getName();
        if (copy.equals("cut")) {
            appService.cutPasteFile(login, current);
        }
        if (copy.equals("copy")) {
            appService.copyPasteFile(login, current);
        }
        Utilities.clearCopyStatus(login);
        return "redirect:/main/".concat(current.toString());
    }
}

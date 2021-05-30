package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/copy")
public class CopyController {

    private FileAppService fileAppService;
    private DirAppService dirAppService;

    @Autowired
    public void setDirAppService(DirAppService dirAppService) {
        this.dirAppService = dirAppService;
    }

    @Autowired
    public void setFileAppService(FileAppService fileAppService) {
        this.fileAppService = fileAppService;
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

//    @GetMapping("/paste")
//    public String paste(Model model,
//            Principal principal,
//                        @RequestParam(value = "copy") String copy,
//                        @RequestParam(name = "current_dir_id") Long current) {
//        String login = principal.getName();
//        if (copy.equals("cut")) {
//            fileAppService.cutPasteFile(login, current);
//        }
//        if (copy.equals("copy")) {
//            fileAppService.copyPasteFile(login, current);
//        }
//        Utilities.clearCopyStatus(login);
//        return "redirect:/main/".concat(current.toString());
//    }

    @GetMapping("/paste")
    public String paste(Model model,
                        Principal principal,
                        @RequestParam(value = "copy") String copy,
                        @RequestParam(name = "current_dir_id") Long current) {
        String login = principal.getName();
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
        List<FileApp> files = dir.getFiles();
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(current));
        List<DirApp> links = Utilities.getLinks(principal.getName(), dir);
        String cutOrCopy = Utilities.showCutOrCopy(principal.getName());
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("current_dir", dir);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("links", links);
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("duplicate", true);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, principal.getName()));
        return "page_views/main";
    }

    @GetMapping("/replacement")
    public String paste(Principal principal,
                        @RequestParam(value = "copy") String copy,
                        @RequestParam(name = "current_dir_id") Long current) {
        String login = principal.getName();
        fileAppService.replacement(login, copy, current);
        Utilities.clearCopyStatus(login);
        return "redirect:/main/".concat(current.toString());
    }
}


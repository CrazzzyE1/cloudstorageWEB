package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.AppService;
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
    private AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping()
    public String mainPage(Model model, Principal principal) {
        Utilities.clearLinks(principal.getName());
        DirApp dirRoot = appService.getRootDirId(principal.getName());
        List<FileApp> files = dirRoot.getFiles();
        Long id = dirRoot.getId();
        List<DirApp> dirs = appService.getDirsByDirParentId(Math.toIntExact(id));
        model.addAttribute("space", Utilities.formatSize(appService.getFilesSpace(principal.getName())));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        String cutOrCopy = Utilities.showCutOrCopy(principal.getName());
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(appService, principal.getName()));
        return "page_views/main";
    }

    @GetMapping("/{id}")
    public String changeDirectory(Principal principal,
                                  @PathVariable(value = "id") Long id,
                                  Model model) {
        DirApp dir = appService.getDirById(id);
        List<FileApp> files = dir.getFiles();
        List<DirApp> dirs = appService.getDirsByDirParentId(Math.toIntExact(id));
        List<DirApp> links = Utilities.getLinks(principal.getName(), dir);
        String cutOrCopy = Utilities.showCutOrCopy(principal.getName());
        model.addAttribute("space", Utilities.formatSize(appService.getFilesSpace(principal.getName())));
        model.addAttribute("current_dir", dir);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("links", links);
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(appService, principal.getName()));
        return "page_views/main";
    }

    @GetMapping("/new")
    public String createNewDir(@RequestParam(value = "name") String name,
                               @RequestParam(value = "parent_id") Integer parent_id,
                               @RequestParam(value = "id") Integer id) {
        appService.createNewDir(name, parent_id, id);
        String tmp = "redirect:".concat(parent_id.toString());
        return tmp;
    }

    @GetMapping("/delete")
    public String deleteDir(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "parent_id") Integer parent_id) {
        appService.removeDir(id);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/deletef")
    public String deleteFile(Principal principal,
                             @RequestParam(value = "id") Long id,
                             @RequestParam(value = "parent_id") Integer parent_id) {
        DirApp dirTo = appService.getRootDirId(principal.getName().concat("_recycle"));
        appService.moveFile(id, dirTo);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "search") String filename, Principal principal) {
        DirApp dirRoot = appService.getRootDirId(principal.getName());
        // TODO: 17.05.2021 FIX back to root after delete file
        List<FileApp> files = appService.getFilesByParams(dirRoot.getUser().getId(), filename);
        model.addAttribute("space", Utilities.formatSize(appService.getFilesSpace(principal.getName())));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("files", files);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(appService, principal.getName()));
        return "page_views/main";
    }
}

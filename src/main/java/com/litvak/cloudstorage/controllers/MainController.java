package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.AppService;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/main")
public class MainController {
    private AppService appService;
    // TODO: 13.05.2021 FIX links
    private List<DirApp> links = new ArrayList<>();

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping()
    public String mainPage(Model model, Principal principal){
        DirApp dirRoot = appService.getRootDirId(principal.getName());
        List<FileApp> files = dirRoot.getFiles();
        Long id = dirRoot.getId();
        List<DirApp> dirs = appService.getDirsByDirParentId(Math.toIntExact(id));
        model.addAttribute("space", appService.getFilesSpace(dirRoot.getUser().getId()));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        return "page_views/main";
    }

    @GetMapping("/{id}")
    public String changeDirectory(@PathVariable(value = "id") Long id,
                                  Model model, @RequestParam (name = "back", required = false) String back)
    {
        DirApp dir = appService.getDirById(id);
        List<FileApp> files = dir.getFiles();
        List<DirApp> dirs = appService.getDirsByDirParentId(Math.toIntExact(id));
        if(back != null && !back.isBlank() && !back.isEmpty() && back.equals("back")) {
            links.remove(links.size() - 1);
        } else {
            links.add(dir);
        }

        model.addAttribute("progress", "width: 5%");
        model.addAttribute("space", appService.getFilesSpace(dir.getUser().getId()));
        model.addAttribute("current_dir", dir);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("links", links);
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
    public String deleteFile(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "parent_id") Integer parent_id) {
        appService.removeFile(id);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "search") String filename, Principal principal) {
        DirApp dirRoot = appService.getRootDirId(principal.getName());
        // TODO: 14.05.2021 FIX IT id

        Long id = dirRoot.getId();
        List<FileApp> files = appService.getFilesByParams(dirRoot.getUser().getId(), filename);
        model.addAttribute("space", appService.getFilesSpace(dirRoot.getUser().getId()));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("files", files);
        return "page_views/main";
    }
}

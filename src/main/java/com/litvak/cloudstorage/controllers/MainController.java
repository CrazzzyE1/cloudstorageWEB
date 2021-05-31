package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
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
    public String mainPage(Model model, Principal principal,
                           @PathVariable(value = "duplicate", required = false) boolean duplicate
    ) {
        Utilities.clearLinks(principal.getName());
        DirApp dirRoot = dirAppService.getRootDir(principal.getName());
        List<FileApp> files = dirRoot.getFiles();
        Long id = dirRoot.getId();
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(id));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        String cutOrCopy = Utilities.showCutOrCopy(principal.getName());
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
        return "page_views/main";
    }

    @GetMapping("/{id}")
    public String changeDirectory(Principal principal,
                                  @PathVariable(value = "id") Long id,
                                  @PathVariable(value = "duplicate", required = false) boolean duplicate,
                                  Model model) {
        DirApp dir = dirAppService.getDirById(id);
        List<FileApp> files = dir.getFiles();
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(id));
        List<DirApp> links = Utilities.getLinks(principal.getName(), dir);
        String cutOrCopy = Utilities.showCutOrCopy(principal.getName());
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
        model.addAttribute("current_dir", dir);
        model.addAttribute("directories", dirs);
        model.addAttribute("files", files);
        model.addAttribute("links", links);
        model.addAttribute("copy", cutOrCopy);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
        return "page_views/main";
    }

    @GetMapping("/new")
    public String createNewDir(@RequestParam(value = "name") String name,
                               @RequestParam(value = "parent_id") Integer parent_id,
                               @RequestParam(value = "id") Integer id) {
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
        DirApp dirTo = dirAppService.getRootDir(principal.getName().concat("_recycle"));
        fileAppService.moveFile(id, dirTo);
        return "redirect:".concat(parent_id.toString());
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "search") String filename, Principal principal) {
        DirApp dirRoot = dirAppService.getRootDir(principal.getName());
        List<FileApp> files = fileAppService.getFilesByParams(dirRoot.getUser().getId(), filename);
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("files", files);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
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

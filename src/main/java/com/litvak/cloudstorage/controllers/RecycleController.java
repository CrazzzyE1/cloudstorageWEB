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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/recycle")
public class RecycleController {

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

    @GetMapping
    public String showRecycleBin(Model model,
                                 Principal principal) {
        DirApp dirRoot = dirAppService.getRootDir(principal.getName().concat("_recycle"));
        List<FileApp> files = dirRoot.getFiles();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("files", files);
        return "page_views/recycle";
    }

    @GetMapping("/restore")
    public String restoreFile(Model model,
                              Principal principal,
                              @RequestParam(name = "id") Long id) {
        DirApp dirTo = dirAppService.getRootDir(principal.getName());
        FileApp fileApp = fileAppService.getFileById(id).get();
        if (Utilities.checkingFileNameForDuplication(fileApp.getName(), dirTo.getFiles())) {
            model.addAttribute("duplicate", true);
            model.addAttribute("id", id);
            DirApp dirRoot = dirAppService.getRootDir(principal.getName().concat("_recycle"));
            List<FileApp> files = dirRoot.getFiles();
            model.addAttribute("current_dir", dirRoot);
            model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
            model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
            model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
            model.addAttribute("files", files);
            return "page_views/recycle";
        }
        fileAppService.moveFile(id, dirTo);
        return "redirect:/recycle";
    }

    @GetMapping("/restoreFile")
    public String restoreFile (Principal principal,
                               @RequestParam(name = "id") Long id) {
        DirApp dirRoot = dirAppService.getRootDir(principal.getName());
        FileApp fileApp = fileAppService.getFileById(id).get();
        fileAppService.deleteByNameAndDirApp(fileApp.getName(), dirRoot);
        fileAppService.moveFile(id, dirRoot);
        return "redirect:/recycle";
    }

    @GetMapping("/deleteQuestion")
    public String deleteFileQ(@RequestParam(name = "id") Long id,
                              Model model,
                              Principal principal) {
        model.addAttribute("delete", true);
        model.addAttribute("id", id);
        DirApp dirRoot = dirAppService.getRootDir(principal.getName().concat("_recycle"));
        List<FileApp> files = dirRoot.getFiles();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
        model.addAttribute("files", files);
        return "page_views/recycle";
    }

    @GetMapping("/deleteAllQuestion")
    public String deleteFileQ(Model model,
                              Principal principal) {
        model.addAttribute("deleteall", true);
        DirApp dirRoot = dirAppService.getRootDir(principal.getName().concat("_recycle"));
        List<FileApp> files = dirRoot.getFiles();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
        model.addAttribute("files", files);
        return "page_views/recycle";
    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam(name = "id") Long id) {
        String nameSystem = fileAppService.getFileById(id).get().getNameSystem();
        fileAppService.removeFile(id);
        List<FileApp> files = fileAppService.getAllFilesByNameSystem(nameSystem);
        if (files.size() == 0) Utilities.removePhysicalFile(nameSystem);
        return "redirect:/recycle";
    }

    @GetMapping("/restoreall")
    public String restoreAll(Principal principal,
                             Model model) {
        boolean flag = false;
        String dirName = principal.getName();
        DirApp recycle = dirAppService.getRootDir(dirName.concat("_recycle"));
        DirApp dirTo = dirAppService.getRootDir(dirName);
        List<FileApp> files = recycle.getFiles();
        for (int i = 0; i < files.size(); i++) {
            if (Utilities.checkingFileNameForDuplication(files.get(i).getName(), dirTo.getFiles())) {
                flag = true;
                continue;
            }
            fileAppService.moveFile(files.get(i).getId(), dirTo);
        }
        if(flag) {
            model.addAttribute("duplicate2", true);
            files = recycle.getFiles();
            model.addAttribute("current_dir", recycle);
            model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, principal.getName()));
            model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(principal.getName())));
            model.addAttribute("storage", Utilities.formatSize(userService.getStorage(principal.getName())));
            model.addAttribute("files", files);
            return "page_views/recycle";
        }
        return "redirect:/main";
    }

    @GetMapping("/restoreAllFile")
    public String restoreAllFile(Principal principal){
        String dirName = principal.getName();
        DirApp dir = dirAppService.getRootDir(principal.getName());
        DirApp recycle = dirAppService.getRootDir(dirName.concat("_recycle"));
        List<FileApp> files = recycle.getFiles();
        for (int i = 0; i < files.size(); i++) {
            String name = files.get(i).getName();
            fileAppService.deleteByNameAndDirApp(name, dir);
            fileAppService.moveFile(files.get(i).getId(), dir);
        }
        return "redirect:/main";
    }

    @GetMapping("/deleteall")
    public String deleteAll(Principal principal) {
        String dirName = principal.getName();
        DirApp recycle = dirAppService.getRootDir(dirName.concat("_recycle"));
        List<FileApp> files = fileAppService.getAllFilesByDir(recycle);
        fileAppService.removeAll(files);
        List<FileApp> tmp;
        String nameTmp;
        for (int i = 0; i < files.size(); i++) {
            nameTmp = files.get(i).getNameSystem();
            tmp = fileAppService.getAllFilesByNameSystem(nameTmp);
            if (tmp.size() == 0) Utilities.removePhysicalFile(nameTmp);
        }
        return "redirect:/main";
    }
}

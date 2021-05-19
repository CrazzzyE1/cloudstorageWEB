package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.AppService;
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

    AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public String showRecycleBin(Model model, Principal principal) {

        DirApp dirRoot = appService.getRootDirId(principal.getName().concat("_recycle"));
        List<FileApp> files = dirRoot.getFiles();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(appService, principal.getName()));
        model.addAttribute("space", Utilities.formatSize(appService.getFilesSpace(principal.getName())));
        model.addAttribute("files", files);
        return "page_views/recycle";
    }

    @GetMapping("/restore")
    public String restoreFile(Principal principal,
                              @RequestParam(name = "id") Long id) {
        DirApp dirTo = appService.getRootDirId(principal.getName());
        FileApp fileApp = appService.getFileById(id).get();
        if(Utilities.checkingFileNameForDuplication(fileApp.getName(), dirTo.getFiles())) return "redirect:/recycle";
        appService.moveFile(id, dirTo);
        return "redirect:/recycle";
    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam(name = "id") Long id) {
        String nameSystem = appService.getFileById(id).get().getNameSystem();
        appService.removeFile(id);
        List<FileApp> files = appService.getAllFilesByNameSystem(nameSystem);
        if(files.size() == 0) Utilities.removePhysicalFile(nameSystem);
        return "redirect:/recycle";
    }


    @GetMapping("/restoreall")
    public String restoreAll(Principal principal) {
        String dirName = principal.getName();
        DirApp recycle = appService.getRootDirId(dirName.concat("_recycle"));
        DirApp dirTo = appService.getRootDirId(dirName);
        List<FileApp> files = recycle.getFiles();
        for (int i = 0; i < files.size(); i++) {
            if(Utilities.checkingFileNameForDuplication(files.get(i).getName(), dirTo.getFiles())) continue;
            appService.moveFile(files.get(i).getId(), dirTo);
        }
        return "redirect:/main";
    }

    @GetMapping("/deleteall")
    public String deleteAll(Principal principal) {
        String dirName = principal.getName();
        DirApp recycle = appService.getRootDirId(dirName.concat("_recycle"));
        List<FileApp> files = appService.getAllFilesByDir(recycle);
        appService.removeAll(files);
        List<FileApp> tmp;
        String nameTmp;
        for (int i = 0; i < files.size(); i++) {
            nameTmp = files.get(i).getNameSystem();
            tmp = appService.getAllFilesByNameSystem(nameTmp);
            if(tmp.size() == 0) Utilities.removePhysicalFile(nameTmp);
        }
        return "redirect:/main";
    }
}

package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.AppService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/upload")
public class UploadController {

    AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public String showUploadPage(@RequestParam(name = "current_dir_id") Long id, Model model){
        model.addAttribute("current_dir_id", id);
        return "page_views/upload";
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(name = "current_dir_id") Long id ){
        String name = file.getOriginalFilename();
        if(name == null || name.isEmpty()) return "redirect:/main/".concat(String.valueOf(id));
        List<FileApp> files = appService.getAllFilesByDir(appService.getDirById(id));
        while (Utilities.checkingFileNameForDuplication(name, files)) {
            name = "COPY - ".concat(name);
        }
        String nameSystem = Utilities.uploadFile(file, name);
        if(nameSystem == null) return "redirect:/main/".concat(String.valueOf(id));
        Long size = file.getSize();
        DirApp dirApp = appService.getDirById(id);
        FileApp fileApp = new FileApp(null, name, nameSystem, size,
                Utilities.formatSize(size), LocalTime.now().toString().split("\\.")[0],
                LocalDate.now().toString(), dirApp);
        appService.createNewFile(fileApp);
        return "redirect:/main/".concat(String.valueOf(id));
    }
}

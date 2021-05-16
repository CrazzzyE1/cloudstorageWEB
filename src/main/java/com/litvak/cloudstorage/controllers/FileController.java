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

@Controller
@RequestMapping("/upload")
public class FileController {

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
        String nameSystem = Utilities.uploadFile(file);
        if(nameSystem == null) return "redirect:/main/".concat(String.valueOf(id));
        Long size = file.getSize();
        DirApp dirApp = appService.getDirById(id);
        FileApp fileApp = new FileApp();
        fileApp.setName(name);
        fileApp.setNameSystem(nameSystem);
        fileApp.setSize(size);
        fileApp.setStrsize(Utilities.formatSize(size));
        fileApp.setTime(LocalTime.now().toString().split("\\.")[0]);
        fileApp.setDate(LocalDate.now().toString());
        fileApp.setDirApp(dirApp);
        appService.createNewFile(fileApp);
        return "redirect:/main/".concat(String.valueOf(id));
    }
}

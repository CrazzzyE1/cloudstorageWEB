package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/upload")
public class UploadController {

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

    @GetMapping
    public String showUploadPage(@RequestParam(name = "current_dir_id") Long id, Model model) {
        model.addAttribute("current_dir_id", id);
        return "page_views/upload";
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(name = "current_dir_id") Long id) {
        String name = file.getOriginalFilename();
        if (name == null || name.isEmpty()) return "redirect:/main/".concat(String.valueOf(id));
        List<FileApp> files = fileAppService.getAllFilesByDir(dirAppService.getDirById(id));
        while (Utilities.checkingFileNameForDuplication(name, files)) {
            name = "COPY - ".concat(name);
        }
        String nameSystem = Utilities.uploadFile(file, name);
        if (nameSystem == null) return "redirect:/main/".concat(String.valueOf(id));
        Long size = file.getSize();
        DirApp dirApp = dirAppService.getDirById(id);
        FileApp fileApp = new FileApp(null, name, nameSystem, size,
                Utilities.formatSize(size), LocalTime.now().toString().split("\\.")[0],
                LocalDate.now().toString(), dirApp);
        fileAppService.saveFile(fileApp);
        return "redirect:/main/".concat(String.valueOf(id));
    }
}

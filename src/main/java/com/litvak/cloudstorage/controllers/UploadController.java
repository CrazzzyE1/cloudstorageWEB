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
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/upload")
public class UploadController {

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
    public String showUploadPage(@RequestParam(name = "current_dir_id") Long id,
                                 Model model) {
        model.addAttribute("current_dir_id", id);
        return "page_views/upload";
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(name = "current_dir_id") Long id,
                             Principal principal) {
        String name = file.getOriginalFilename();
        if (name == null || name.isEmpty()) return "redirect:/main/".concat(String.valueOf(id));
        if (userService.getStorage(principal.getName()) - fileAppService.getFilesSpace(principal.getName()) - file.getSize() <= 0) {
            return "redirect:/main/".concat(String.valueOf(id));
        }
        List<FileApp> files = fileAppService.getAllFilesByDir(dirAppService.getDirById(id));
        name = Utilities.getNameIfDuplicate(name, files);
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

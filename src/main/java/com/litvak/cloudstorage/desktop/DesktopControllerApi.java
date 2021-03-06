package com.litvak.cloudstorage.desktop;

import com.litvak.cloudstorage.services.DesktopServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api")
public class DesktopControllerApi {

    DesktopServiceImpl desktopService;

    @Autowired
    public void setDesktopService(DesktopServiceImpl desktopService) {
        this.desktopService = desktopService;
    }

    @GetMapping
    public String query(@RequestParam(name = "query") String query) {
        return desktopService.getAnswer(query);
    }

    @GetMapping("/download")
    public byte[] download(@RequestParam(name = "name") String name,
                           @RequestParam(name = "dir") String dir) {
        name = name.replace("??", " ");
        return desktopService.getFile(name, dir);
    }

    @PostMapping("/upload")
    public String upload(@Param("file") MultipartFile file,
                         @Param("name") String name,
                         @Param("dir") String dir) {
        if (!desktopService.uploadFile(file, name, dir)) return "uploadFail";
        return "uploadSuccess";
    }
}

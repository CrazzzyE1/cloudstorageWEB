package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class IndexController {

    private AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public String indexPage() {
        return "page_views/index";
    }

    @GetMapping("/reg")
    public String registrationPage() {
        return "page_views/registration";
    }

    @GetMapping("/auth")
    public String authPage(Model model,
                           @RequestParam(name = "login") Optional<String> login,
                           @RequestParam(name = "password") Optional<String> password) {

        System.out.println(login.get() + " *** " + password.get());
        appService.setRootDir(login.get());
        Long id = appService.getRootDirId(login.get()).getId();
        String tmp = "redirect:/main/";
        tmp.concat(id.toString());
        return tmp;
    }
}
//        System.out.println(appService.getAllUsers());
//        String fileName = "user_folder";
//        Path path = Paths.get(fileName);
//        if (!Files.exists(path)) {
//            Files.createDirectory(path);
//            System.out.println("Directory is created");
//        } else {
//            System.out.println("Directory Exists");
//        }
//
//        File file = new File(path.toString());
//        File[] files = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            BasicFileAttributes attr = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
//            System.out.println(files[i].getName() + " - " + files[i].length() + " - " + attr.lastModifiedTime().toString().split("T")[0] + " - "
//                    + attr.isDirectory());
//        }
//        model.addAttribute("fileslist", Arrays.asList(files));
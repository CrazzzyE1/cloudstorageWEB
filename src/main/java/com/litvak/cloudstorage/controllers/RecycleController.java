package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                 Principal principal,
                                 @RequestParam(name = "login", required = false) String login) {
        if (login == null) {
            if (Utilities.getSelect(principal.getName()) != null) {
                login = Utilities.getSelect(principal.getName());
            } else {
                login = principal.getName();
            }
        }
        DirApp dirRoot = dirAppService.getRootDir(login.concat("_recycle"));
//        List<FileApp> files = dirRoot.getFiles();
//        String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
//        List<User> users = userService.getAllUsers();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("files", dirRoot.getFiles());
        model.addAttribute("login", login);
        model.addAttribute("role", userService.getUserByUsername(principal.getName()).getRoles()
                .stream().findFirst().get().getName());
        model.addAttribute("users", userService.getAllUsers());
        return "page_views/recycle";
    }

    @GetMapping("/restore")
    public String restoreFile(Model model,
                              Principal principal,
                              @RequestParam(name = "login") String login,
                              @RequestParam(name = "id") Long id) {
        DirApp dirTo = dirAppService.getRootDir(login);
        FileApp fileApp = fileAppService.getFileById(id).get();
        if (Utilities.checkingFileNameForDuplication(fileApp.getName(), dirTo.getFiles())) {
            model.addAttribute("duplicate", true);
            model.addAttribute("id", id);
            DirApp dirRoot = dirAppService.getRootDir(login.concat("_recycle"));
//            List<FileApp> files = dirRoot.getFiles();
//            String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
//            List<User> users = userService.getAllUsers();
            model.addAttribute("current_dir", dirRoot);
            model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
            model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
            model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
            model.addAttribute("files", dirRoot.getFiles());
            model.addAttribute("login", login);
            model.addAttribute("role", userService.getUserByUsername(principal.getName()).getRoles()
                    .stream().findFirst().get().getName());
            model.addAttribute("users", userService.getAllUsers());
            return "page_views/recycle";
        }
        fileAppService.moveFile(id, dirTo);
        return "redirect:/recycle";
    }

    @GetMapping("/restoreFile")
    public String restoreFile(@RequestParam(name = "login") String login,
                              @RequestParam(name = "id") Long id) {
        DirApp dirRoot = dirAppService.getRootDir(login);
        FileApp fileApp = fileAppService.getFileById(id).get();
        fileAppService.deleteByNameAndDirApp(fileApp.getName(), dirRoot);
        fileAppService.moveFile(id, dirRoot);
        return "redirect:/recycle";
    }

    @GetMapping("/deleteQuestion")
    public String deleteFileQ(Model model,
                              Principal principal,
                              @RequestParam(name = "id") Long id,
                              @RequestParam(name = "login") String login) {
        model.addAttribute("delete", true);
        model.addAttribute("id", id);
        DirApp dirRoot = dirAppService.getRootDir(login.concat("_recycle"));
//        List<FileApp> files = dirRoot.getFiles();
//        String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
//        List<User> users = userService.getAllUsers();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("files", dirRoot.getFiles());
        model.addAttribute("login", login);
        model.addAttribute("role", userService.getUserByUsername(principal.getName()).getRoles()
                .stream().findFirst().get().getName());
        model.addAttribute("users", userService.getAllUsers());
        return "page_views/recycle";
    }

    @GetMapping("/deleteAllQuestion")
    public String deleteFileQ(Model model,
                              Principal principal,
                              @RequestParam(name = "login") String login) {
        model.addAttribute("deleteall", true);
        DirApp dirRoot = dirAppService.getRootDir(login.concat("_recycle"));
//        List<FileApp> files = dirRoot.getFiles();
//        String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
//        List<User> users = userService.getAllUsers();
        model.addAttribute("current_dir", dirRoot);
        model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
        model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
        model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
        model.addAttribute("files", dirRoot.getFiles());
        model.addAttribute("login", login);
        model.addAttribute("role", userService.getUserByUsername(principal.getName()).getRoles()
                .stream().findFirst().get().getName());
        model.addAttribute("users", userService.getAllUsers());
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
                             @RequestParam(name = "login") String login,
                             Model model) {
        boolean flag = false;
        Map<String, FileApp> toMove = new HashMap<>();
        DirApp recycle = dirAppService.getRootDir(login.concat("_recycle"));
        DirApp dirTo = dirAppService.getRootDir(login);
        List<FileApp> files = recycle.getFiles();
        for (int i = 0; i < files.size(); i++) {
            if (Utilities.checkingFileNameForDuplication(files.get(i).getName(), dirTo.getFiles())) {
                flag = true;
            }
            toMove.put(files.get(i).getName(), files.get(i));
        }
        if (flag) {
            model.addAttribute("duplicate2", true);
            files = recycle.getFiles();
            String role = userService.getUserByUsername(principal.getName()).getRoles().stream().findFirst().get().getName();
            List<User> users = userService.getAllUsers();
            model.addAttribute("current_dir", recycle);
            model.addAttribute("percent", Utilities.getPercentForProgressBar(fileAppService, userService, login));
            model.addAttribute("space", Utilities.formatSize(fileAppService.getFilesSpace(login)));
            model.addAttribute("storage", Utilities.formatSize(userService.getStorage(login)));
            model.addAttribute("files", files);
            model.addAttribute("login", login);
            model.addAttribute("role", role);
            model.addAttribute("users", users);
            return "page_views/recycle";
        } else {
            for (Map.Entry<String, FileApp> entry : toMove.entrySet()) {
                fileAppService.moveFile(entry.getValue().getId(), dirTo);
            }
        }
        return "redirect:/main";
    }

    @GetMapping("/restoreAllFile")
    public String restoreAllFile(@RequestParam(name = "login") String login) {
        Map<String, FileApp> toMove = new HashMap<>();
        DirApp dir = dirAppService.getRootDir(login);
        DirApp recycle = dirAppService.getRootDir(login.concat("_recycle"));
        List<FileApp> recycleFiles = fileAppService.getAllFilesByDir(recycle);
//        List<FileApp> files = fileAppService.getAllFilesByDir(dir);
        for (int i = 0; i < recycleFiles.size(); i++) {
            toMove.put(recycleFiles.get(i).getName(), recycleFiles.get(i));
        }
        recycleFiles = new ArrayList<>(toMove.values());
        for (int i = 0; i < recycleFiles.size(); i++) {
            String name = recycleFiles.get(i).getName();
            fileAppService.deleteByNameAndDirApp(name, dir);
            fileAppService.moveFile(recycleFiles.get(i).getId(), dir);
        }
        return "redirect:/main";
    }

    @GetMapping("/deleteall")
    public String deleteAll(@RequestParam(name = "login") String login) {
        DirApp recycle = dirAppService.getRootDir(login.concat("_recycle"));
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

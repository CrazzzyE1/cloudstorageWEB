package com.litvak.cloudstorage.desktop;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandController {
    FileAppService fileAppService;
    DirAppService dirAppService;
    UserService userService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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

    public String auth(String query) {
        System.out.println(query);
        String login = query.split(" ")[1];
        String password = query.split(" ")[2];
        User user = userService.getUserByUsername(login);
        if (user == null || !user.isEnabled()) return "authfail 0 0";
        String passwordMatch = user.getPassword();
        System.out.println(passwordEncoder.matches(password, passwordMatch));
        if (!passwordEncoder.matches(password, passwordMatch)) {
            return "authfail 0 0";
        }
        String space = String.valueOf(userService.getStorage(login) / 1000000);
        String current_id = dirAppService.getRootDir(login).getId().toString();
        return "authsuccess ".concat(space).concat(" ").concat(current_id);
    }

    public String ls(String currentDir) {
        Long id = Long.valueOf(currentDir);
        StringBuilder sb = new StringBuilder();
        DirApp dir = dirAppService.getDirById(id);
        List<DirApp> dirs = dirAppService.getDirsByDirParentId(Math.toIntExact(dir.getId()));
        List<FileApp> files = fileAppService.getAllFilesByDir(dir);
        for (DirApp d : dirs) {
            sb.append("[DIR]".concat(d.getName().replace(" ", "??"))).append(" ");
        }
        for (FileApp f : files) {
            sb.append(f.getName().replace(" ", "??")).append(" ");
        }
        if (sb.length() < 1) sb.append("Empty");
        return sb.toString();
    }

    public String getAddress(String query) {
        StringBuilder sb = new StringBuilder();
        List<String> tmp = new ArrayList<>();
        Long id = Long.valueOf(query.split(" ")[1]);
        DirApp dir = dirAppService.getDirById(id);
        while (dir.getDirId() != null) {
            tmp.add("/".concat(dir.getName()));
            dir = dirAppService.getDirById(Long.valueOf(dir.getDirId()));
        }
        for (int i = tmp.size() - 1; i >= 0; i--) {
            sb.append(tmp.get(i)).append(" ");
        }
        return sb.toString();
    }

    public String checkSpace(String login) {
        return fileAppService.getFilesSpace(login).toString();
    }

    public String reg(String query) {
        String login = query.split(" ")[1];
        String password = query.split(" ")[2];
        password = passwordEncoder.encode(password);
        if (userService.createNewUser(login, password) != null) {
            return "regsuccess";
        }
        return "regfail";
    }

    public String cd(String query) {
        String dirName = query.split(" ")[1].replace("??", " ");
        String currentDir = query.split(" ")[2];
        System.out.println("cur dir: " + dirName);
        DirApp dir;
        if (dirName.equals("back")) {
            dir = dirAppService.getDirById(Long.valueOf(currentDir));
            if (dir.getDirId() != null) currentDir = dir.getDirId().toString();
        } else {
            dir = dirAppService.getDirByNameAndDirApp(dirName, Integer.valueOf(currentDir));
            if (dir == null) return "success ".concat(currentDir);
            currentDir = dir.getId().toString();
        }
        return "success ".concat(currentDir);
    }

    public String mkdir(String query) {
        String folderName = query.split(" ")[1].replace("??", " ");
        Long id = Long.valueOf(query.split(" ")[2]);
        if (Utilities.checkingFolderNameForDuplication(folderName,
                dirAppService.getDirsByDirParentId(Math.toIntExact(id)))) {
            return "dirFail";
        }
        dirAppService.createNewDir(folderName, Math.toIntExact(id),
                Math.toIntExact(dirAppService.getDirById(id).getUser().getId()));
        return "dirSuccess";
    }


    public String rm(String query) {
        String name = query.split(" ")[1].replace("??", " ");
        Long id = Long.valueOf(query.split(" ")[2]);
        DirApp dir = dirAppService.getDirByNameAndDirApp(name, Math.toIntExact(id));
        if (dir == null) return "rmFail";
        dirAppService.removeDir(dir.getId());
        return "rmSuccess";
    }

    public String rmf(String query) {
        String name = query.split(" ")[1].replace("??", " ");
        Long id = Long.valueOf(query.split(" ")[2]);
        DirApp dir = dirAppService.getDirById(id);
        FileApp file = fileAppService.getFileByNameAndDirApp(name, dir);
        if (file == null) return "rmFail";
        name = dir.getUser().getUserName();
        DirApp dirTo = dirAppService.getRootDir(name.concat("_recycle"));
        fileAppService.moveFile(file.getId(), dirTo);
        return "rmSuccess";
    }

    public String search(String query) {
        StringBuilder result = new StringBuilder();
        String search = query.split(" ")[1];
        String login = query.split(" ")[2];
        List<FileApp> files = fileAppService.getFilesByParams(userService.getUserByUsername(login).getId(), search);
        for (int i = 0; i < files.size(); i++) {
            result.append(files.get(i).getName().replace(" ", "??"));
            result.append(" ");
        }
        if (result.length() < 1) result.append("Not Found");
        return result.toString();
    }

    public String changePassword(String query) {
        String login = query.split(" ")[1];
        String oldPass = query.split(" ")[2];
        String newPass = query.split(" ")[3];
        User user = userService.getUserByUsername(login);
        if (passwordEncoder.matches(oldPass, user.getPassword())) {
            userService.changePassword(passwordEncoder.encode(newPass), user);
            return "success";
        }
        return "changeFail";
    }

    public String remove(String query) {
        String login = query.split(" ")[1];
        String oldPass = query.split(" ")[2];
        User user = userService.getUserByUsername(login);
        if (passwordEncoder.matches(oldPass, user.getPassword())) {
            userService.removeAccount(user);
            return "success";
        }
        return "removeFail";
    }

    public String paste(String query) {
        String command = query.split(" ")[1];
        String name = query.split(" ")[2].replace("??", " ");
        Long currentDir = Long.valueOf(query.split(" ")[3]);
        Long dirTo = Long.valueOf(query.split(" ")[4]);
        System.out.println(query);
        DirApp dir = dirAppService.getDirById(currentDir);
        FileApp file = fileAppService.getFileByNameAndDirApp(name, dir);
        if (file == null) return "pasteFail";
        dir = dirAppService.getDirById(dirTo);
        if(Utilities.checkingFileNameForDuplication(name, dir.getFiles())) return "pasteFail";
        if(command.equals("cut")) {
            fileAppService.moveFile(file.getId(), dir);
            return "pasteSuccess";
        }
        if(command.equals("copy")) {
            if(fileAppService.copyFile(file, dir))
            return "pasteSuccess";
        }
        return "pasteFail";
    }

//    public String copyOrCut(String query) {
////        copy 12??12.mp4
//        String command = query.split(" ")[0];
//        String name = query.split(" ")[1].replace("??", " ");
//        String oldPass = query.split(" ")[2];
//    }
}

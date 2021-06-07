package com.litvak.cloudstorage.desktop;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.services.DirAppService;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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
        if(user == null) return "authfail 0";
        String passwordMatch = user.getPassword();
        System.out.println(passwordEncoder.matches(password, passwordMatch));
        if(!passwordEncoder.matches(password,passwordMatch)) {
            return "authfail 0";
        }
        String space = String.valueOf(userService.getStorage(login)/1000000);
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
        System.out.println("FILES FROM LS COMMAND: " + sb);
        return sb.toString();
    }

    // TODO: 05.06.2021 FIX IT 
    public String getAddress() {
        return "AddressAnswerFIXIT";
    }

    public String checkSpace(String login) {
        return fileAppService.getFilesSpace(login).toString();
    }

    public String reg(String query) {
        String login = query.split(" ")[1];
        String password = query.split(" ")[2];
        password = passwordEncoder.encode(password);
        if(userService.createNewUser(login, password) != null) {
            return "regsuccess";
        }
        return "regfail";
    }

    public String cd(String query) {
        String dirName = query.split(" ")[1];
        String currentDir = query.split(" ")[2];
        DirApp dir;
        if(dirName.equals("back")) {
            dir = dirAppService.getDirById(Long.valueOf(currentDir));
            if(dir.getDirId() != null) currentDir = dir.getDirId().toString();
         } else {
            dir = dirAppService.getDirByNameAndDirApp(dirName, Integer.valueOf(currentDir));
            if(dir == null) return "success ".concat(currentDir);
            currentDir = dir.getId().toString();
        }
        return "success ".concat(currentDir);
    }
}

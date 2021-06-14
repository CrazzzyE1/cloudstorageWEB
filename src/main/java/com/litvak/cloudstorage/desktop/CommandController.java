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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
        String login = query.split(" ")[1];
        String password = query.split(" ")[2];
        User user = userService.getUserByUsername(login);
        if (user == null || !user.isEnabled()) return "authfail 0 0";
        String passwordMatch = user.getPassword();
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
        DirApp dir;
        if (dirName.equals("back")) {
            dir = dirAppService.getDirById(Long.valueOf(currentDir));
            if (dir.getDirId() != null) {
                currentDir = dir.getDirId().toString();
            } else {
                String login = dir.getUser().getUserName();
                return "success ".concat(dirAppService.getRootDir(login).getId().toString());
            }
        } else {
            dir = dirAppService.getDirByNameAndDirApp(dirName, Integer.valueOf(currentDir));
            if (dir == null) return "success ".concat(currentDir);
            currentDir = dir.getId().toString();
        }
        return "success ".concat(currentDir);
    }

    public String mkdir(String query) {
        String folderName = query.split(" ")[1].replace("??", " ");
        long id = Long.parseLong(query.split(" ")[2]);
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
        long id = Long.parseLong(query.split(" ")[2]);
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
        DirApp dir = dirAppService.getDirById(currentDir);
        FileApp file = fileAppService.getFileByNameAndDirApp(name, dir);
        if (file == null) return "pasteFail";
        dir = dirAppService.getDirById(dirTo);
        if (Utilities.checkingFileNameForDuplication(name, dir.getFiles())) return "pasteFail";
        if (command.equals("cut")) {
            fileAppService.moveFile(file.getId(), dir);
            return "pasteSuccess";
        }
        if (command.equals("copy")) {
            if (fileAppService.copyFile(file, dir))
                return "pasteSuccess";
        }
        return "pasteFail";
    }

    public String recycle(String query) {
        String login = query.split(" ")[1];
        DirApp dirApp = dirAppService.getDirByNameAndDirApp(login.concat("_recycle"), null);
        return dirApp.getId().toString();
    }

    public String recycleClean(String query) {
        String login = query.split(" ")[1];
        DirApp recycleBin = dirAppService.getDirByNameAndDirApp(login.concat("_recycle"), null);
        List<FileApp> files = fileAppService.getAllFilesByDir(recycleBin);
        fileAppService.removeAll(files);
        List<FileApp> tmp;
        String nameTmp;
        for (int i = 0; i < files.size(); i++) {
            nameTmp = files.get(i).getNameSystem();
            tmp = fileAppService.getAllFilesByNameSystem(nameTmp);
            if (tmp.size() == 0) Utilities.removePhysicalFile(nameTmp);
        }
        return "recycleSuccess";
    }

    public String restore(String query) {
        String login = query.split(" ")[1];
        DirApp root = dirAppService.getRootDir(login);
        DirApp recycleBin = dirAppService.getDirByNameAndDirApp(login.concat("_recycle"), null);
        List<FileApp> restoreFiles = fileAppService.getAllFilesByDir(recycleBin);
        List<FileApp> files = fileAppService.getAllFilesByDir(root);
        Map<String, FileApp> toMove = new HashMap<>();
        boolean flag;
        for (int i = 0; i < restoreFiles.size(); i++) {
            flag = false;
            for (int j = 0; j < files.size(); j++) {
                if (restoreFiles.get(i).getName().equals(files.get(j).getName())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                toMove.put(restoreFiles.get(i).getName(), restoreFiles.get(i));
            }
        }
        for (Map.Entry<String, FileApp> entry : toMove.entrySet()) {
            fileAppService.moveFile(entry.getValue().getId(), root);
        }
        return "restoreSuccess";
    }

    public byte[] getFile(String name, String dirId) {
        DirApp dir = dirAppService.getDirById(Long.valueOf(dirId));
        FileApp file = fileAppService.getFileByNameAndDirApp(name, dir);
        if (file == null) return null;
        String sysName = file.getNameSystem();
        byte[] bytes = new byte[file.getSize().intValue()];
        try {
            bytes = Files.readAllBytes(Path.of("users_files/" + sysName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public boolean uploadFile(MultipartFile file, String name, String dir) {
        DirApp dirApp = dirAppService.getDirById(Long.valueOf(dir));
        FileApp tmp = fileAppService.getFileByNameAndDirApp(name, dirApp);
        if (tmp != null) return false;
        String sysName = Utilities.createSystemName(name);
        Path path = Path.of("users_files/".concat(sysName));
        if (Files.exists(path)) {
            return false;
        } else {
            try {
                Files.createFile(path);
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileApp fileApp = new FileApp(null, name, sysName, file.getSize(),
                Utilities.formatSize(file.getSize()), LocalTime.now().toString().split("\\.")[0],
                LocalDate.now().toString(), dirApp);
        fileAppService.saveFile(fileApp);
        return true;
    }
}

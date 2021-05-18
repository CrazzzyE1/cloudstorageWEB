package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.repositories.DirAppRepository;
import com.litvak.cloudstorage.repositories.FileAppRepository;
import com.litvak.cloudstorage.repositories.UserRepository;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    private UserRepository userRepository;
    private DirAppRepository dirAppRepository;
    private FileAppRepository fileAppRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setDirAppRepository(DirAppRepository dirAppRepository) {
        this.dirAppRepository = dirAppRepository;
    }

    @Autowired
    public void setFileAppRepository(FileAppRepository fileAppRepository) {
        this.fileAppRepository = fileAppRepository;
    }

    @Transactional
    public DirApp getRootDirId(String login) {
        return dirAppRepository.findDirAppByName(login);
    }

    @Transactional
    public List<DirApp> getDirsByDirParentId(Integer id) {
        return dirAppRepository.findAllByDirId(id);
    }

    @Transactional
    public List<FileApp> getFilesByParams(Long userId, String search) {
        return fileAppRepository.findAllByUserIdAndSearchLine(search, userId);
    }

//    @Transactional
//    public Long getFilesSpace(Long userId) {
//        Long size = fileAppRepository.findSpaceSize(userId);
//        if (size == null) size = 0L;
//        return size;
//    }

    @Transactional
    public Long getFilesSpace(String login) {
        Long size = fileAppRepository.findSpaceSize(login);
        if (size == null) size = 0L;
        return size;
    }

    @Transactional
    public User getUserByUsername(String username) {
        return userRepository.findUserByUserName(username);
    }

    @Transactional
    public DirApp getDirById(Long id) {
        return dirAppRepository.findDirAppsById(id);
    }

    @Transactional
    public DirApp createNewDir(String name, Integer parentId, Integer userId) {
        DirApp dirApp = new DirApp();
        dirApp.setName(name);
        dirApp.setDirId(parentId);
        dirApp.setUser(userRepository.findById(Long.valueOf(userId)).get());
        return dirAppRepository.save(dirApp);
    }

    @Transactional
    public void removeDir(Long id) {
        dirAppRepository.deleteById(id);
    }

    @Transactional
    public void removeFile(Long id) {
        fileAppRepository.deleteById(id);
    }

    @Transactional
    public User createNewUser(String login, String password) {
        DirApp dir = new DirApp();
        User user = new User();
        user.setUserName(login);
        user.setEnabled(true);
        user.setPassword(password);
        dir.setName(login);
        dir.setUser(user);
        userRepository.save(user);
        dirAppRepository.save(dir);
        return user;
    }

    @Transactional
    public void createNewFile(FileApp fileApp) {
        fileAppRepository.save(fileApp);
    }

    @Transactional
    public Optional<FileApp> getFileById(Long id) {
        return fileAppRepository.findById(id);
    }

    @Transactional
    public void removeAccount(Principal principal) {
        User user = userRepository.findUserByUserName(principal.getName());
        user.setEnabled(false);
        System.out.println(user);
    }

    @Transactional
    public User changePassword(String oldPass, String newPass, Principal principal) {
        String login = principal.getName();
        oldPass = passwordEncoder.encode(oldPass);
        newPass = passwordEncoder.encode(newPass);
        User user = userRepository.findUserByUserName(login);
        user.setPassword(newPass);
        return user;
    }

    @Transactional
    public void cutPasteFile(String login, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp file = fileAppRepository.findById(fileId).get();
        DirApp dir = dirAppRepository.findDirAppsById(current);
        file.setDirApp(dir);
    }

    @Transactional
    public void copyPasteFile(String login, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp tmpFile = fileAppRepository.findById(fileId).get();
        DirApp dirApp = dirAppRepository.findDirAppsById(current);
        FileApp file = new FileApp();
        file.setName(tmpFile.getName());
        file.setNameSystem(tmpFile.getNameSystem());
        file.setSize(tmpFile.getSize());
        file.setStrsize(tmpFile.getStrsize());
        file.setTime(LocalTime.now().toString().split("\\.")[0]);
        file.setDate(LocalDate.now().toString());
        file.setDirApp(dirApp);
        fileAppRepository.save(file);
    }
}

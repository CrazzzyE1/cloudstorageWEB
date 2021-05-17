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

    @Transactional
    public String getFilesSpace(Long userId) {
        Long size = fileAppRepository.findSpaceSize(userId);
        if (size == null) size = 0L;
        return Utilities.formatSize(size);
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
//        userRepository.save(user);
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
}

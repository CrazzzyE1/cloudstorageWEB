package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.repositories.DirAppRepository;
import com.litvak.cloudstorage.repositories.FileAppRepository;
import com.litvak.cloudstorage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    private UserRepository userRepository;
    private DirAppRepository dirAppRepository;
    private FileAppRepository fileAppRepository;
    private String rootDir;

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

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    @Transactional
    public User getUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    @Transactional
    public DirApp getRootDirId (String login) {
        return dirAppRepository.findDirAppByName(login);
    }

    @Transactional
    public  List<DirApp> getDirsByDirParentId(Integer id) {
        return dirAppRepository.findAllByDirId(id);
    }

    @Transactional
    public List<FileApp> getFilesByParams(Long userId, String search) {
        return fileAppRepository.findAllByUserIdAndSearchLine(search, userId);
    }

    @Transactional
    public String getFilesSpace(Long userId) {
        Integer size = fileAppRepository.findSpaceSize(userId);
        return size.toString();
    }


    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User getUserById(Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
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
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void removeUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void removeUser(User user) {
        userRepository.delete(user);
    }
}

package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.repositories.DirAppRepository;
import com.litvak.cloudstorage.repositories.UserRepository;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirAppServiceImpl implements DirAppService {
    private DirAppRepository dirAppRepository;
    private UserRepository userRepository;

    @Autowired
    public void setDirAppRepository(DirAppRepository dirAppRepository) {
        this.dirAppRepository = dirAppRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public DirApp saveDir(DirApp dir) {
        dirAppRepository.save(dir);
        return dirAppRepository.save(dir);
    }

    @Override
    public DirApp getRootDir(String login) {
        return dirAppRepository.findDirAppByName(login).get();
    }

    @Override
    public List<DirApp> getDirsByDirParentId(Integer id) {
        return dirAppRepository.findAllByDirId(id);
    }

    @Override
    public DirApp getDirById(Long id) {
        return dirAppRepository.findDirAppsById(id).get();
    }

    @Override
    public void createNewDir(String name, Integer parentId, Integer userId) {
        name = name.trim();
        List<DirApp> folders = dirAppRepository.findAllByDirId(parentId);
        if (Utilities.checkingFolderNameForDuplication(name, folders)) return;
        DirApp dirApp = new DirApp();
        dirApp.setName(name);
        dirApp.setDirId(parentId);
        dirApp.setUser(userRepository.findById(Long.valueOf(userId)).get());
        dirAppRepository.save(dirApp);
    }

    @Override
    public void removeDir(Long id) {
        dirAppRepository.deleteById(id);
    }
}

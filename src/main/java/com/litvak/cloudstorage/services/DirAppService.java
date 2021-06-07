package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.DirApp;

import java.util.List;

public interface DirAppService {
    DirApp saveDir(DirApp dir);

    DirApp getRootDir(String login);

    List<DirApp> getDirsByDirParentId(Integer id);

    DirApp getDirById(Long id);

    void createNewDir(String name, Integer parentId, Integer userId);

    void removeDir(Long id);

    DirApp getDirByNameAndDirApp(String name, Integer dirId);
}

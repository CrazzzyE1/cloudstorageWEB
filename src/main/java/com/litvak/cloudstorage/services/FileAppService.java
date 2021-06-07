package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;

import java.util.List;
import java.util.Optional;

public interface FileAppService {
    List<FileApp> getFilesByParams(Long userId, String search);

    Long getFilesSpace(String login);

    void removeFile(Long id);

    void removeAll(List<FileApp> files);

    void saveFile(FileApp fileApp);

    Optional<FileApp> getFileById(Long id);

    boolean cutPasteFile(String login, Long current);

    void moveFile(Long fileId, DirApp dirTo);

    boolean copyPasteFile(String login, Long current);

    List<FileApp> getAllFilesByDir(DirApp dirApp);

    List<FileApp> getAllFilesByNameSystem(String nameSystem);

    void replacement(String login, String copy, Long current);

    void deleteByNameAndDirApp(String name, DirApp dir);

    FileApp getFileByNameAndDirApp(String name,DirApp dir);
}

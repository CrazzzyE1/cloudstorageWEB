package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.repositories.DirAppRepository;
import com.litvak.cloudstorage.repositories.FileAppRepository;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileAppServiceImpl implements FileAppService {
    private FileAppRepository fileAppRepository;
    private DirAppRepository dirAppRepository;

    @Autowired
    public void setFileAppRepository(FileAppRepository fileAppRepository) {
        this.fileAppRepository = fileAppRepository;
    }

    @Autowired
    public void setDirAppRepository(DirAppRepository dirAppRepository) {
        this.dirAppRepository = dirAppRepository;
    }

    @Transactional
    @Override
    public List<FileApp> getFilesByParams(Long userId, String search) {
        return fileAppRepository.findAllByUserIdAndSearchLine(search.toLowerCase(Locale.ROOT), userId);
    }

    @Override
    public Long getFilesSpace(String login) {
        Long size = fileAppRepository.findSpaceSize(login);
        if (size == null) size = 0L;
        return size;
    }

    @Transactional
    @Override
    public void removeFile(Long id) {
        fileAppRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void removeAll(List<FileApp> files) {
        fileAppRepository.deleteAll(files);
    }

    @Transactional
    @Override
    public void saveFile(FileApp fileApp) {
        fileAppRepository.save(fileApp);
    }

    @Override
    public Optional<FileApp> getFileById(Long id) {
        return fileAppRepository.findById(id);
    }

    @Transactional
    @Override
    public boolean cutPasteFile(String login, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp file = fileAppRepository.findById(fileId).get();
        DirApp dir = dirAppRepository.findDirAppsById(current).get();
        if (Utilities.checkingFileNameForDuplication(file.getName(), dir.getFiles())) return false;
        file.setDirApp(dir);
        return true;
    }

    @Transactional
    @Override
    public void moveFile(Long fileId, DirApp dirTo) {
        FileApp file = fileAppRepository.findById(fileId).get();
        file.setDirApp(dirTo);
    }

    @Transactional
    @Override
    public boolean copyPasteFile(String login, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp tmpFile = fileAppRepository.findById(fileId).get();
        DirApp dirApp = dirAppRepository.findDirAppsById(current).get();
        FileApp file = new FileApp();
        if (Utilities.checkingFileNameForDuplication(tmpFile.getName(), dirApp.getFiles())) return false;
        file.setName(tmpFile.getName());
        file.setNameSystem(tmpFile.getNameSystem());
        file.setSize(tmpFile.getSize());
        file.setStrsize(tmpFile.getStrsize());
        file.setTime(LocalTime.now().toString().split("\\.")[0]);
        file.setDate(LocalDate.now().toString());
        file.setDirApp(dirApp);
        fileAppRepository.save(file);
        return true;
    }

    @Transactional
    @Override
    public boolean copyFile(FileApp tmpFile, DirApp dirTo) {
        FileApp file = new FileApp();
        file.setName(tmpFile.getName());
        file.setNameSystem(tmpFile.getNameSystem());
        file.setSize(tmpFile.getSize());
        file.setStrsize(tmpFile.getStrsize());
        file.setTime(LocalTime.now().toString().split("\\.")[0]);
        file.setDate(LocalDate.now().toString());
        file.setDirApp(dirTo);
        fileAppRepository.save(file);
        return true;
    }

    @Override
    public List<FileApp> getAllFilesByDir(DirApp dirApp) {
        return fileAppRepository.findAllByDirApp(dirApp);
    }

    @Override
    public List<FileApp> getAllFilesByNameSystem(String nameSystem) {
        return fileAppRepository.findAllByNameSystem(nameSystem);
    }

    @Override
    @Transactional
    public void deleteByNameAndDirApp(String name, DirApp dir) {
        fileAppRepository.deleteByNameAndDirApp(name, dir);
    }

    @Override
    @Transactional
    public void replacement(String login, String copy, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp file = fileAppRepository.findById(fileId).get();
        String name = file.getName();
        DirApp dir = dirAppRepository.findDirAppsById(current).get();
        if (Objects.equals(dir, file.getDirApp())) return;
        fileAppRepository.deleteByNameAndDirApp(name, dir);
        if ("cut".equals(copy)) {
            file.setDirApp(dir);
            fileAppRepository.save(file);
            return;
        }
        if ("copy".equals(copy)) {
            FileApp fileNew = new FileApp();
            fileNew.setName(name);
            fileNew.setNameSystem(file.getNameSystem());
            fileNew.setSize(file.getSize());
            fileNew.setStrsize(file.getStrsize());
            fileNew.setTime(LocalTime.now().toString().split("\\.")[0]);
            fileNew.setDate(LocalDate.now().toString());
            fileNew.setDirApp(dir);
            fileAppRepository.save(fileNew);
        }
    }

    @Override
    public FileApp getFileByNameAndDirApp(String name, DirApp dir) {
        Optional<FileApp> op = fileAppRepository.findByNameAndDirApp(name, dir);
        return op.orElse(null);
    }
}

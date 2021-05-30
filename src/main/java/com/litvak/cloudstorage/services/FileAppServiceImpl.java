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

    @Transactional
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

    @Transactional
    @Override
    public Optional<FileApp> getFileById(Long id) {
        return fileAppRepository.findById(id);
    }

//    @Transactional
//    @Override
//    public void cutPasteFile(String login, Long current) {
//        Long fileId = Utilities.getFileId(login);
//        FileApp file = fileAppRepository.findById(fileId).get();
//        DirApp dir = dirAppRepository.findDirAppsById(current).get();
//        if (Utilities.checkingFileNameForDuplication(file.getName(), dir.getFiles())) return;
//        file.setDirApp(dir);
//    }

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

//    @Transactional
//    @Override
//    public void copyPasteFile(String login, Long current) {
//        Long fileId = Utilities.getFileId(login);
//        FileApp tmpFile = fileAppRepository.findById(fileId).get();
//        DirApp dirApp = dirAppRepository.findDirAppsById(current).get();
//        FileApp file = new FileApp();
//        if (Utilities.checkingFileNameForDuplication(tmpFile.getName(), dirApp.getFiles())) return;
//        file.setName(tmpFile.getName());
//        file.setNameSystem(tmpFile.getNameSystem());
//        file.setSize(tmpFile.getSize());
//        file.setStrsize(tmpFile.getStrsize());
//        file.setTime(LocalTime.now().toString().split("\\.")[0]);
//        file.setDate(LocalDate.now().toString());
//        file.setDirApp(dirApp);
//        fileAppRepository.save(file);
//    }

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
    public List<FileApp> getAllFilesByDir(DirApp dirApp) {
        return fileAppRepository.findAllByDirApp(dirApp);
    }

    @Transactional
    @Override
    public List<FileApp> getAllFilesByNameSystem(String nameSystem) {
        return fileAppRepository.findAllByNameSystem(nameSystem);
    }

    @Override
    @Transactional
    public void replacement(String login, String copy, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp file = fileAppRepository.findById(fileId).get();
        String name = file.getName();
        DirApp dir = dirAppRepository.findDirAppsById(current).get();
        fileAppRepository.deleteByNameAndDirApp(name, dir);
        if("cut".equals(copy)) {
            file.setDirApp(dir);
            fileAppRepository.save(file);
            return;
        }
        System.out.println(copy);
        if("copy".equals(copy)) {
            System.out.println("Herr");
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
}

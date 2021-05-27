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

    @Override
    public void removeFile(Long id) {
        fileAppRepository.deleteById(id);
    }

    @Override
    public void removeAll(List<FileApp> files) {
        fileAppRepository.deleteAll(files);
    }

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
    public void cutPasteFile(String login, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp file = fileAppRepository.findById(fileId).get();
        DirApp dir = dirAppRepository.findDirAppsById(current);
        if (Utilities.checkingFileNameForDuplication(file.getName(), dir.getFiles())) return;
        file.setDirApp(dir);
    }

    @Transactional
    @Override
    public void moveFile(Long fileId, DirApp dirTo) {
        FileApp file = fileAppRepository.findById(fileId).get();
        file.setDirApp(dirTo);
    }

    @Transactional
    @Override
    public void copyPasteFile(String login, Long current) {
        Long fileId = Utilities.getFileId(login);
        FileApp tmpFile = fileAppRepository.findById(fileId).get();
        DirApp dirApp = dirAppRepository.findDirAppsById(current);
        FileApp file = new FileApp();
        if (Utilities.checkingFileNameForDuplication(tmpFile.getName(), dirApp.getFiles())) return;
        file.setName(tmpFile.getName());
        file.setNameSystem(tmpFile.getNameSystem());
        file.setSize(tmpFile.getSize());
        file.setStrsize(tmpFile.getStrsize());
        file.setTime(LocalTime.now().toString().split("\\.")[0]);
        file.setDate(LocalDate.now().toString());
        file.setDirApp(dirApp);
        fileAppRepository.save(file);
    }

    @Override
    public List<FileApp> getAllFilesByDir(DirApp dirApp) {
        return fileAppRepository.findAllByDirApp(dirApp);
    }

    @Override
    public List<FileApp> getAllFilesByNameSystem(String nameSystem) {
        return fileAppRepository.findAllByNameSystem(nameSystem);
    }
}

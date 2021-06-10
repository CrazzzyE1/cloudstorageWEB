package com.litvak.cloudstorage.services;

import org.springframework.web.multipart.MultipartFile;

public interface DesktopService {
    String getAnswer(String query);

    byte[] getFile(String name, String dir);

    boolean uploadFile(MultipartFile file, String name, String dir);
}

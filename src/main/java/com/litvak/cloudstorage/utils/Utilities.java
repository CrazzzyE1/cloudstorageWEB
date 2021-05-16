package com.litvak.cloudstorage.utils;

import com.litvak.cloudstorage.services.AppService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class Utilities {

    public static String uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String name = file.getOriginalFilename();
                String nameSystem = UUID.randomUUID().toString();
                nameSystem = nameSystem.concat(".").concat(name);
                String rootPath = "users_files";
                File dir = new File(rootPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File uploadedFile = new File(rootPath + File.separator + nameSystem);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));
                stream.write(bytes);
                stream.flush();
                stream.close();
                return nameSystem;

            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String formatSize(Long sizeInt) {
        String tmp = "";
        Double size = Double.valueOf(sizeInt);
        if (size > 1023) {
            size = size / 1024;
            if (size > 1023) {
                size /= 1024;
                if (size > 1023) {
                    size /= 1024;
                    if (size > 1023) {
                        size /= 1024;
                        tmp = tmp.concat(String.format("%.2f", (size))).concat(" ").concat("TiB");
                    } else {
                        tmp = tmp.concat(String.format("%.2f", (size))).concat(" ").concat("GiB");
                    }
                } else {
                    tmp = tmp.concat(String.format("%.2f", (size))).concat(" ").concat("MiB");
                }
            } else {
                tmp = tmp.concat(String.format("%.2f", (size))).concat(" ").concat("KiB");
            }
        } else {
            tmp = tmp.concat(String.format("%.2f", (size))).concat(" ").concat("B");
        }
        return tmp;
    }

    public static void downloadFile(Long id, AppService appService, HttpServletResponse response) {
        String fileNameSystem = appService.getFileById(id).get().getNameSystem();
        String fileName = appService.getFileById(id).get().getName();

        String dataDirectory = ("users_files");
        Path file = Paths.get(dataDirectory, fileNameSystem);
        if (Files.exists(file)) {
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8").replace("+", " ");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

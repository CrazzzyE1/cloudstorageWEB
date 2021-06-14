package com.litvak.cloudstorage.utils;

import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.FileApp;
import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class Utilities {
    private static Map<String, List<DirApp>> linksMap = new HashMap<>();
    private static Map<String, Long> cutMap = new HashMap<>();
    private static Map<String, String> selectMap = new HashMap<>();
    private static Map<String, Long> copyMap = new HashMap<>();

    /**
     * Проверка имени папки на дубликат в папке назначения
     */
    public static boolean checkingFolderNameForDuplication(String name, List<DirApp> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка имени файла на дубликат в папке назначения
     */
    public static boolean checkingFileNameForDuplication(String name, List<FileApp> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Подготовка данных о проценте заполнения Диска для "Прогресс бара"
     */
    public static String getPercentForProgressBar(FileAppService fileAppService, UserService userService, String login) {
        Long space = fileAppService.getFilesSpace(login);
        long defaultSpace = userService.getStorage(login);
        space = space * 100 / defaultSpace;
        if (space > 100L) space = 100L;
        return space.toString();
    }

    /**
     * Получение id файла из Map, готового для копирования или перемещения
     */
    public static Long getFileId(String login) {
        Long id = 0L;
        if (cutMap.get(login) != null) id = cutMap.get(login);
        if (copyMap.get(login) != null) id = copyMap.get(login);
        return id;
    }

    /**
     * Очитска данных о перемещаемом или копируемом файле
     */
    public static void clearCopyStatus(String login) {
        cutMap.remove(login);
        copyMap.remove(login);
    }

    /**
     * Сохранение данных о перемещаемом файле
     */
    public static void saveCutFileId(String login, Long id) {
        copyMap.remove(login);
        cutMap.put(login, id);
    }

    /**
     * Сохранение данных о копируемом файле
     */
    public static void saveCopyFileId(String login, Long id) {
        cutMap.remove(login);
        copyMap.put(login, id);
    }

    /**
     * Очистка данных о ссылках и пути перемещения по каталогам диска
     */
    public static void clearLinks(String login) {
        if (linksMap.get(login) != null) {
            linksMap.put(login, new ArrayList<>());
        }
    }

    /**
     * Определение типа операции Copy или Cut при вставке файла
     */
    public static String showCutOrCopy(String login) {
        if (cutMap.get(login) != null) return "cut";
        if (copyMap.get(login) != null) return "copy";
        return null;
    }

    /**
     * Получение списка ссылкок и пути перемещения по каталогам диска
     */
    public static List<DirApp> getLinks(String login, DirApp dir) {
        List<DirApp> links;
        if (dir.getDirId() == null) {
            links = new ArrayList<>();
            linksMap.put(login, links);
            return links;
        }
        if (linksMap.get(login) == null) {
            linksMap.put(login, new ArrayList<>());
            links = linksMap.get(login);
        } else {
            links = linksMap.get(login);
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getId().equals(dir.getId())) {
                    links = links.subList(0, i + 1);
                    return links;
                }
            }
        }
        links.add(dir);
        return links;
    }

    /**
     * Загрузка внешнего файла в хранилище
     */
    public static String uploadFile(MultipartFile file, String filename) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String nameSystem = UUID.randomUUID().toString();
                nameSystem = nameSystem.concat(".").concat(filename);
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

    /**
     * Форматирование значения размера файла или дискового пространства в User-friendly формат
     */
    public static String formatSize(Long sizeInt) {
        String tmp = "";
        int count = 0;
        Double size = Double.valueOf(sizeInt);
        while (size / 1024 >= 1) {
            size /= 1024;
            count++;
        }
        tmp = tmp.concat(String.format("%.2f", (size))).concat(" ");
        switch (count) {
            case 0:
                tmp = tmp.concat("B");
                break;
            case 1:
                tmp = tmp.concat("KiB");
                break;
            case 2:
                tmp = tmp.concat("MiB");
                break;
            case 3:
                tmp = tmp.concat("GiB");
                break;
        }
        return tmp;
    }

    /**
     * Скачивание файла на ПК пользователя с заменой системного имени файла на фактический.
     */
    public static void downloadFile(Long id, FileAppService fileAppService, HttpServletResponse response) {
        String fileNameSystem = fileAppService.getFileById(id).get().getNameSystem();
        String fileName = fileAppService.getFileById(id).get().getName();
        String dataDirectory = ("users_files");
        Path file = Paths.get(dataDirectory, fileNameSystem);
        if (Files.exists(file)) {
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            try {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", " ");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Метод для удаления физического файла.
     */
    public static boolean removePhysicalFile(String nameSystem) {
        boolean res = false;
        String rootPath = "users_files";
        File fileToRemove = new File(rootPath + File.separator + nameSystem);
        if (fileToRemove.exists()) res = fileToRemove.delete();
        return res;
    }

    /**
     * Сохранение выбранного пользователя в режиме ADMIN
     */
    public static void saveSelect(String name, String login) {
        selectMap.put(name, login);
    }

    /**
     * Получение выбранного пользователя в режиме ADMIN
     */
    public static String getSelect(String name) {
        return selectMap.get(name);
    }

    /**
     * Создание системного имени файла.
     */
    public static String createSystemName(String name) {
        String nameSystem = UUID.randomUUID().toString();
        return nameSystem.concat(".").concat(name);
    }

    /**
     * Поиск дубликатов имени при востановлении файлов из корзины
     * */
    public static boolean checkNameAndSaveRestoreFiles(boolean flag, List<FileApp> files, DirApp dirTo, Map<String, FileApp> toMove) {
        for (int i = 0; i < files.size(); i++) {
            if (Utilities.checkingFileNameForDuplication(files.get(i).getName(), dirTo.getFiles())) {
                flag = true;
            }
            toMove.put(files.get(i).getName(), files.get(i));
        }
        return flag;
    }

    public static void saveRestoreFilesToMap(Map<String, FileApp> toMove, List<FileApp> recycleFiles) {
        for (int i = 0; i < recycleFiles.size(); i++) {
            toMove.put(recycleFiles.get(i).getName(), recycleFiles.get(i));
        }
    }

    public static void checkAndDeletePhysicalFiles(List<FileApp> files, FileAppService fileAppService) {
        List<FileApp> tmp;
        String nameTmp;
        for (int i = 0; i < files.size(); i++) {
            nameTmp = files.get(i).getNameSystem();
            tmp = fileAppService.getAllFilesByNameSystem(nameTmp);
            if (tmp.size() == 0) Utilities.removePhysicalFile(nameTmp);
        }
    }

    public static void restoreFilesWithReplace(List<FileApp> recycleFiles, FileAppService fileAppService, DirApp dir) {
        for (int i = 0; i < recycleFiles.size(); i++) {
            String name = recycleFiles.get(i).getName();
            fileAppService.deleteByNameAndDirApp(name, dir);
            fileAppService.moveFile(recycleFiles.get(i).getId(), dir);
        }
    }
}
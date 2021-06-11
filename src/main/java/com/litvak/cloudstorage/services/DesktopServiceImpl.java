package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.desktop.CommandController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DesktopServiceImpl implements DesktopService {
    String msg;
    CommandController commandController;

    @Autowired
    public void setCommandController(CommandController commandController) {
        this.commandController = commandController;
    }

    @Override
    public String getAnswer(String query) {
        String command = query.split(" ")[0];
        switch (command) {
            case ("ls"):
                msg = commandController.ls(query.split(" ")[1]);
                break;
            case ("auth"):
                msg = commandController.auth(query);
                break;
            case ("checkSpace"):
                msg = commandController.checkSpace(query.split(" ")[1]);
                break;
            case ("getAddress"):
                msg = commandController.getAddress(query);
                break;
            case ("reg"):
                msg = commandController.reg(query);
                break;
            case ("mkdir"):
                msg = commandController.mkdir(query);
                break;
            case ("rm"):
                msg = commandController.rm(query);
                break;
            case ("rmf"):
                msg = commandController.rmf(query);
                break;
            case ("cd"):
                msg = commandController.cd(query);
                break;
            case ("search"):
                msg = commandController.search(query);
                break;
            case ("change"):
                msg = commandController.changePassword(query);
                break;
            case ("remove"):
                msg = commandController.remove(query);
                break;
            case ("paste"):
                msg = commandController.paste(query);
                break;
            case ("recycle"):
                msg = commandController.recycle(query);
                break;
            case ("recycleClean"):
                msg = commandController.recycleClean(query);
                break;
            case ("restore"):
                msg = commandController.restore(query);
                break;
            default:
                System.out.println("Unknown command");
                break;
        }
        return msg;
    }

    @Override
    public byte[] getFile(String name, String dir) {
        return commandController.getFile(name, dir);
    }

    @Override
    public boolean uploadFile(MultipartFile file, String name, String dir) {
        return commandController.uploadFile(file, name, dir);
    }
}


package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.desktop.CommandController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DesktopServiceImpl implements DesktopService{
    String msg;
    CommandController commandController;

    @Autowired
    public void setCommandController(CommandController commandController) {
        this.commandController = commandController;
    }

    @Override
    public String getAnswer(String query) {
        String command = query.split(" ")[0];
        System.out.println("Command: " + command);
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
                msg = commandController.getAddress();
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
//                case ("remove"):
//                msg = commandController.remove(command);
//                break;
//            case ("copy"):
//            case ("cut"):
//                msg = commandController.copyOrCut(command);
//                break;
//            case ("paste"):
//                msg = commandController.paste();
//                break;
//            case ("search"):
//                msg = commandController.search(command);
//                break;
//            case ("download"):
//                msg = commandController.download(command);
//                break;
//            case ("waiting"):
//                downloadFlag = true;
//                break;
//            case ("waitingUpload"):
//                uploadFlag = true;
//                System.out.println("UploadSize: " + command[1]);
//                uploadFileSize = Long.parseLong(command[1]);
//                break;
//            case ("upload"):
//                msg = commandController.upload(command);
//                break;
//            case ("recycleClean"):
//                msg = commandController.recycleClean();
//                break;
//            case ("restore"):
//                msg = commandController.restore();
//                break;
//            case ("change"):
//                msg = commandController.changePassword(command);
//                break;
            default:
                System.out.println("Unknown command");
                break;
        }
        return msg;
    }
}


package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.FileAppService;
import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/download")
public class DownloadController {
    private FileAppService fileAppService;

    @Autowired
    public void setFileAppService(FileAppService fileAppService) {
        this.fileAppService = fileAppService;
    }

    @RequestMapping()
    public void downloadPDFResource(HttpServletResponse response,
                                    @RequestParam(name = "id") Long id) {
        Utilities.downloadFile(id, fileAppService, response);
    }
}

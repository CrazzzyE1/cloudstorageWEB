package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.AppService;

import com.litvak.cloudstorage.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/download")
public class DownloadController {
    AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @RequestMapping()
    public void downloadPDFResource(HttpServletResponse response,
                                    @RequestParam(name = "id") Long id) {
        Utilities.downloadFile(id, appService, response);
    }
}

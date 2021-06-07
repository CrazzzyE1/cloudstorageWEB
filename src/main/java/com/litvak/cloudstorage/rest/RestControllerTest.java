package com.litvak.cloudstorage.rest;

import com.litvak.cloudstorage.services.DesktopServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestControllerTest {

    DesktopServiceImpl desktopService;

    @Autowired
    public void setDesktopService(DesktopServiceImpl desktopService) {
        this.desktopService = desktopService;
    }

    @GetMapping
    public String test(@RequestParam(name = "query") String query) {
        System.out.println(query);
        return desktopService.getAnswer(query);
    }
}

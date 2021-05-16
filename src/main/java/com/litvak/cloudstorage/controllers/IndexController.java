package com.litvak.cloudstorage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String indexPage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "page_views/login";
    }

    @GetMapping("/logout")
    public String logoutPage(){
        return "page_views/logout";
    }
}

package com.litvak.cloudstorage.controllers;

import com.litvak.cloudstorage.services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

@Controller
@RequestMapping("/")
public class IndexController {

    private AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public String indexPage() {
        return "page_views/index";
    }

    @GetMapping("/reg")
    public String registrationPage(){
        return "page_views/registration";
    }

    @GetMapping("/main")
    public String mainPage(){
        return "page_views/main";
    }

    @GetMapping("/auth")
     public String authPage(Model model) throws IOException {
        String fileName = "user_folder";
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            System.out.println("Directory is created");
        } else {
            System.out.println("Directory Exists");
        }

        File file = new File(path.toString());
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            BasicFileAttributes attr = Files.readAttributes(files[i].toPath(), BasicFileAttributes.class);
            System.out.println(files[i].getName() + " - " + files[i].length() + " - " + attr.lastModifiedTime().toString().split("T")[0] + " - "
                    + attr.isDirectory());
        }

        model.addAttribute("fileslist", Arrays.asList(files));

        return "page_views/main";
    }


//        @GetMapping("/{id}")
//        public String editProduct(@PathVariable(value = "id") Long id,
//                                  Model model) {
//            model.addAttribute("product", productService.getById(id));
//            return "product_views/product_form";
//        }
//
//        @PostMapping("/product_update")
//        public String updateProduct(Product product) {
//            productService.addOrUpdate(product);
//            return "redirect:/product";
//        }
//
//        @GetMapping("/new")
//        public String newProduct(Model model) {
//            model.addAttribute(new Product());
//            return "product_views/product_form";
//        }
//
//        @GetMapping("/delete/{id}")
//        public String removeProduct(@PathVariable(value = "id") Long id) {
//            productService.remove(id);
//            return "redirect:/product";
//        }

}

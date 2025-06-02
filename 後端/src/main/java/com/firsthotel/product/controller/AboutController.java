package com.firsthotel.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String aboutPage() {
        return "about";  // 假設你有 about.html 文件在 templates 目錄下
    }
}

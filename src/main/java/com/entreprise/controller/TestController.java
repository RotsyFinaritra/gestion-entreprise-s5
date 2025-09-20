package com.entreprise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    
    @GetMapping("/test-upload")
    public String testUpload() {
        return "test-upload";
    }
}

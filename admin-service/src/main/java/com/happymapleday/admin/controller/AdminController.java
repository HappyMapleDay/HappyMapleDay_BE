package com.happymapleday.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/health")
    public String health() {
        return "Admin Service is running!";
    }

    @GetMapping("/status")
    public String status() {
        return "Admin Service is healthy and ready!";
    }
}

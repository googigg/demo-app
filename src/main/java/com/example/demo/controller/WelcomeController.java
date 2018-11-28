package com.example.demo.controller;


import com.example.demo.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private DemoService demoService;

    public WelcomeController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/demo")
    public String demo() {
        return demoService.google();
    }
}

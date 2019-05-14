package com.foxwho.demo.controller;

import com.foxwho.demo.service.UidGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UidController {
    @Autowired
    private UidGenService uidGenService;

    @GetMapping("/uidGenerator")
    public String UidGenerator() {
        return String.valueOf(uidGenService.getUid());
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}

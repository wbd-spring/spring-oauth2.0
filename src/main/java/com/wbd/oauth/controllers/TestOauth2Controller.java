package com.wbd.oauth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestOauth2Controller {

	@GetMapping("/hi")
    public String hi(String name){
        return "hi , " + name;
    }
}

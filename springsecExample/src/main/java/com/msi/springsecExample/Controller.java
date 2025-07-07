package com.msi.springsecExample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
 public class Controller {
    @GetMapping("/")
    public String greet() {
        return "Hello World!";
    }
}

package fac.bpm2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/meteo")
public class GetMeteoController {
    @GetMapping
    public String getMeteo() {
        System.out.println("Exec");
        return "il fait beau connard";
    }
}

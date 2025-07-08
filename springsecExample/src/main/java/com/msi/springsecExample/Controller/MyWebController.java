package com.msi.springsecExample.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody; // Gardez cet import si vous avez d'autres méthodes REST

@Controller // Assurez-vous que c'est @Controller et non @RestController
public class MyWebController {

    // Si vous avez une méthode pour la racine qui renvoie du texte directement, utilisez @ResponseBody
    @GetMapping("/")
    @ResponseBody
    public String greet() {
        return "Hello World!";
    }



    @GetMapping("/home")
    public String showHomePage() {
        return "home"; // Renvoie le nom de la vue
    }
}

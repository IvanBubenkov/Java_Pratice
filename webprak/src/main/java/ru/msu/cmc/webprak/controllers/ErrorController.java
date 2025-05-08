package ru.msu.cmc.webprak.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Provide your custom error view or message
        return "error";  // Ensure "errorPage" corresponds to an actual view
    }
}

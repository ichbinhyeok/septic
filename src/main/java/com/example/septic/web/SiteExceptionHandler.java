package com.example.septic.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class SiteExceptionHandler {
    @ExceptionHandler(StateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleStateNotFound(StateNotFoundException exception, Model model) {
        model.addAttribute("page", new PageMeta("State Not Found", exception.getMessage()));
        model.addAttribute("message", exception.getMessage());
        return "pages/not-found";
    }
}

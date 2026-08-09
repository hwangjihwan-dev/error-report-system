package com.hwang.errorreport.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String hadnleIllegalArgumentException(IllegalArgumentException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error/custom-error";
    }
}

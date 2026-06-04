package com.hwang.errorreport.controller;

import com.hwang.errorreport.dto.auth.SignupRequest;
import com.hwang.errorreport.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm(@ModelAttribute SignupRequest signupRequest){
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "auth/signup";
        }
        try{
            userService.signup(signupRequest);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("loginId","duplicate",e.getMessage());
            return "auth/signup";
        }


        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "auth/login";
    }
}

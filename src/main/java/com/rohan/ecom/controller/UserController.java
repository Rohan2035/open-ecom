package com.openecom.ecom.controller;

import com.openecom.ecom.dto.LoginDTO;
import com.openecom.ecom.dto.UserRequestDTO;
import com.openecom.ecom.service.LoginService;
import com.openecom.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/refreshToken")
    public Map<String, String> refreshAccessToken(@RequestBody String refreshToken) {
        return Map.of("accessToken", loginService.refreshToken(refreshToken));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }

    @PostMapping("/signup")
    public Map<String, String> signUp(@RequestBody UserRequestDTO requestDTO) {
        userService.addUser(requestDTO);
        return Map.of("Status", "User successfully added");
    }
}

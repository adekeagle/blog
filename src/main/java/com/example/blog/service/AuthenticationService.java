package com.example.blog.service;

import com.example.blog.dto.LoginDto;
import com.example.blog.dto.RegisterDto;

public interface AuthenticationService {

    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}

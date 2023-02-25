package com.example.blog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "Bearer Authentication")
public interface SecuredRestController {
}

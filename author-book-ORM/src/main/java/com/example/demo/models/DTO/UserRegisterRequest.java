package com.example.demo.models.DTO;

import com.example.demo.models.enums.Role;

import java.util.Set;

public record UserRegisterRequest(String username, String password, Set<Role> roles) {
}
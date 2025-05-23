package com.relove.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public class UserRegisterDTO {

    @NotBlank
    @Size(min = 3, max = 25)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 25)
    private String password;

    public UserRegisterDTO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

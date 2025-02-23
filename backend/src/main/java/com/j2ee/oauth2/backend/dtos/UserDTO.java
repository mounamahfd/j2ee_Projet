package com.j2ee.oauth2.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private List<String> roles;
    private boolean active;
}


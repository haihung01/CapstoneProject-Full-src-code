package com.example.triptix.DTO.UserSystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseLoginSuccess {
    private String token;
    private UserSystemDTOview user;
}
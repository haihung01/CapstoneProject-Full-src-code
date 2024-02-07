package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOInTicket {

    private int idUserSystem;

    private String phone;

    private String userName;

    private String fullName;

    private String gender;

    private String email;

    private String role;
}

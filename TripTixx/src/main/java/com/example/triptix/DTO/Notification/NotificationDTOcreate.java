package com.example.triptix.DTO.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDTOcreate {
    private int idUserSystem;

    private String description;
}
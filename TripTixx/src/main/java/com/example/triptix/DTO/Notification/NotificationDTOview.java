package com.example.triptix.DTO.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDTOview {
    private int idNotification;

    private int idUsersystem;

    private String description;

    private long createdDateL;

    private boolean seen;
}
package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotification;

    @ManyToOne
    @JoinColumn(name = "idUsersytem")
    private UserSystem userSystem;

    private String description;

    private String createdDate;

    private boolean seen;
}

package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class News {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNews;

    @ManyToOne
    @JoinColumn(name = "idStaff")
    private UserSystem staff;

    private Date createdDate;

    private Date updatedDate;

    private String description;

    private String title;

    private String imageLink;
}

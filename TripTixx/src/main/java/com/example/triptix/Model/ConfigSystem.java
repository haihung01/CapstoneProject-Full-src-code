package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ConfigSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idConfigSystem;

    private String name;

    private int value;
}
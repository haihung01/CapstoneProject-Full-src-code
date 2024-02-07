package com.example.triptix.DTO.News;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewsDTOview {

    private int idNews;

    private int idStaff;

    @NotBlank(message = "Title is required")
    @Size(min = 5, message = "Title must be at least 5 characters long")
    @Length(max = 255, message = "Title must be less than 255 characters long")
    private String title;

    @NotBlank(message = "Description is required")
    @Length(max = 255, message = "Description must be less than 255 characters long")
    private String description;

    private String createdDate;

    private String updatedDate;

    private String imgLink;
}

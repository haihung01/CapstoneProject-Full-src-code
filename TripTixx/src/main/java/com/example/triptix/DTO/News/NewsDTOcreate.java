package com.example.triptix.DTO.News;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewsDTOcreate {
    private int idStaff;

    @NotBlank(message = "Không để trống tiêu đề.")
    @Size(min = 5, message = "Tiêu đ ít nhất 5 chữ cái.")
    @Length(max = 255, message = "Tiêu đề dài nhất 255 chữ cái.")
    private String title;

    @NotBlank(message = "Không để trống phần mô tả")
    @Length(max = 255, message = "Mô tả dài tối đa 255 chữ cái.")
    private String description;

    @NotNull
    private MultipartFile image;
}

package com.example.triptix.DTO.News;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateObjectImg {
    private int idObj;
    private MultipartFile img;
}

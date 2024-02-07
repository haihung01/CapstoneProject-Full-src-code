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
public class NewsDTOupdate {

    private int idNews;

    private int idStaff;

    @NotBlank(message = "Không để trống tiêu đề.")
    @Size(min = 5, message = "Tiêu đề ít nhất 5 chữ cái.")
    @Length(max = 255, message = "Tiêu đề dài tối đa 255 chữ cái.")
    private String title;

    @NotBlank(message = "Không để trống phần mô tả")
    @Length(max = 255, message = "Mô tả dài tối đa 255 chữ cái.")
    private String description;

    public boolean equalsValue(NewsDTOupdate obj) {
        if(this.idStaff != obj.idStaff) {
            return false;
        }
        if(this.idNews != obj.idNews) {
            return false;
        }
        if (!this.description.equals(obj.description)) {
            return false;
        }
        if (!this.title.equals(obj.title)) {
            return false;
        }
        return true;
    }


}

package com.example.triptix.DTO.SpecialDay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecialDayDTOupdate {
    private int idSpecialDay;

    @Min(value = 0, message = "date long must bigger than 0")
    private Long dateLong;

    @NotBlank(message = "name is required")
    private String name;
}
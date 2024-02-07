package com.example.triptix.DTO.Station;

import com.example.triptix.Util.ValidData.ValueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationDTOcreate {

    @NotBlank(message =  "Nhập tên")
    @Size(min = 5, message = "Tên phãi lớn hơn 5 chữ")
    private String name;
    @NotBlank(message =  "Nhập địa chỉ")
    @Size(min = 5, message = "Địa chỉ phãi lớn hơn 5 chữ")
    private String address;
    @NotBlank(message =  "Nhập tỉnh ")
    @Size(min = 5, message = "Tỉnh phãi lớn hơn 5 chữ. ")
    private String province;

}

package com.example.triptix.DTO.Vehicle;

import com.example.triptix.Util.ValidData.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTOcreate {

    @NotBlank(message = "Không để trống tên xe.")
    @Size(min = 5, message = "Tối thiểu tên xe 5 chữ")
    private String name;

    @Column(unique = true)
    private String licensePlates;

    @NotBlank(message = "Không để trống thể loại xe")
    @ValueType
    private String type;    //type (limousine, giường)


    private String description; //service ( mô tả dv xe có)

    @Min(value = 6, message = "Sức chứa tối thiểu 6 chỗ")
    private short capacity;

    @Min(value = 0, message = "Tầng của xe lớn hơn 0")
    @Max(value = 2, message = "Tầng của xe bé hơn 3")
    private short floor;    //số tầng của xe

    private int idStation;

    @NotNull
    private MultipartFile image;
}

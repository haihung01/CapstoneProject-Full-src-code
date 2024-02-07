package com.example.triptix.DTO.Vehicle;

import com.example.triptix.Util.ValidData.ValueStatus;
import com.example.triptix.Util.ValidData.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTOupdate {
    private int idBus;
    @NotBlank(message = "Không để trống tên xe.")
    @Size(min = 5, message = "Tối thiểu tên xe 5 chữ")
    private String name;
    @NotBlank(message = "Không để trống thể loại xe")
    @ValueType
    private String type;    //type (limousine, giường)

    private String description; //service ( mô tả dv xe có)

    @Min(value = 6, message = "Sức chứa tối thiểu 6 chỗ")
    private short capacity;

    @Min(value = 0, message = "Tầng của xe lớn hơn 0")
    @Max(value = 2, message = "Tầng của xe bé hơn 3")
    private short floor;    //số tầng của xe

    @NotBlank(message = "Không để trống trạng thái.")
    @ValueStatus
    private String status;

    private int idStation;
}

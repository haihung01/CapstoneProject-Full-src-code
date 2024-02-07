package com.example.triptix.DTO.ProvinceCity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetailProvinceCity {
    private String district_id;
    private String district_name;
    private String district_type;
    private String province_id;
}
package com.example.triptix.DTO.ProvinceCity;

import com.example.triptix.Util.ValidData.ProvinceCity.ValueTypeProvinceCity;
import com.example.triptix.Util.ValidData.RegionProvinceCity.ValueRegionProvinceCity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProvinceCityDTOcreate {
    @NotBlank(message = "Province name is required")
    private String name;

    @ValueTypeProvinceCity
    private String type;

    @ValueRegionProvinceCity
    private String region;
}
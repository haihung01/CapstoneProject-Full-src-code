package com.example.triptix.DTO.ProvinceCity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListDetailProvinceCity {
    private List<DetailProvinceCity> results;
}

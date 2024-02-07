package com.example.triptix.Service;


import com.example.triptix.DTO.ProvinceCity.ProvinceCityDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.ProvinceCity;

public interface ProvinceCityService {
    ResponseObject<?> getAll(String type, int pageSize, int pageIndex);
    ResponseObject<?>  getDetail(String id);
    ResponseObject<?>  add(ProvinceCityDTOcreate b);
    ResponseObject<?>  delete(String id);

    ResponseObject<?>  importData();
//    ResponseObject<?>  update(... b);
    ResponseObject<?>  deleteAll();
    boolean checkProvinceVn(String province);
    ProvinceCity getProvinceVn(String provinceName);
//    boolean checkRegionVn(String name1, String name2, String region);
}

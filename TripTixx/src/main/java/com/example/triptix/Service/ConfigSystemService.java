package com.example.triptix.Service;


import com.example.triptix.DTO.ConfigSystem.ConfigSystemDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.ConfigSystem;

public interface ConfigSystemService {
    ResponseObject<?> getAll();
    ResponseObject<?> getDetail(int id);

    ResponseObject<?> create(ConfigSystemDTOcreate b);
    ResponseObject<?> update(ConfigSystem b);
    ResponseObject<?> delete(int id);
}

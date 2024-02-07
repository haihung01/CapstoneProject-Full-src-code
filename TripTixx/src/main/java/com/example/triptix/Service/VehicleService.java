package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Vehicle.VehicleDTOchangeStatus;
import com.example.triptix.DTO.Vehicle.VehicleDTOcreate;
import com.example.triptix.DTO.Vehicle.VehicleDTOupdate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface VehicleService {
    ResponseObject<?> getAll(String type, int pageSize, int pageIndex);
    ResponseObject<?> getDetail(int id);

    ResponseObject<?> create(VehicleDTOcreate b);
    ResponseObject<?> update(VehicleDTOupdate b);
    ResponseObject<?> updateImgNew(int idVehicle, MultipartFile t);
    ResponseObject<?> updateStatus(VehicleDTOchangeStatus b);
    ResponseObject<?> delete(int id);

}

package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.StationTimeCome.StationTimeComeDTOcreate;
import com.example.triptix.Model.key.StationTimeComeKey;

public interface StationTimeComeService {
    ResponseObject<?> getAll(Integer idTrip , int pageSize, int pageIndex);
    ResponseObject<?>  getDetail(StationTimeComeKey idStationTimeCome);

    ResponseObject<?>  create(StationTimeComeDTOcreate b);

    ResponseObject<?> delete(StationTimeComeKey idStationTimeCome);
}

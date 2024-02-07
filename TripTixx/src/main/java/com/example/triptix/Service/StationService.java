package com.example.triptix.Service;

import com.example.triptix.DTO.News.NewsDTOcreate;
import com.example.triptix.DTO.News.NewsDTOupdate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Station.StationDTOchangeStatus;
import com.example.triptix.DTO.Station.StationDTOcreate;
import com.example.triptix.DTO.Station.StationDTOupdate;

public interface StationService {
    ResponseObject<?> getAll(String name, String province , int pageSize, int pageIndex);
    ResponseObject<?>  getDetail(int id);

    ResponseObject<?>  create(StationDTOcreate b);
    ResponseObject<?>  update(StationDTOupdate b);

    ResponseObject<?>  updateStatus(StationDTOchangeStatus b);

    ResponseObject<?> delete(int id);
}

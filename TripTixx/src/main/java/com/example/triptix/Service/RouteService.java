package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Route.RouteDTOchangeStatus;
import com.example.triptix.DTO.Route.RouteDTOcreate;
import com.example.triptix.DTO.Route.RouteDTOupdate;
import com.example.triptix.DTO.Route.RouteDTOview;
import com.example.triptix.DTO.Station.StationDTOchangeStatus;
import com.example.triptix.DTO.Station.StationDTOcreate;
import com.example.triptix.DTO.Station.StationDTOupdate;

public interface RouteService {
    ResponseObject<?> getAll(String codeDepartureDate, String codeEndDate, String name);
    ResponseObject<?> getDetail(int idRoute);
    ResponseObject<?>  create(RouteDTOcreate b);

    ResponseObject<?> update(RouteDTOupdate b);

    ResponseObject<?> delete(int idRoute);

    ResponseObject<?> updateStatus(RouteDTOchangeStatus b);



}

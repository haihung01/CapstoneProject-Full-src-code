package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Trip.*;

import java.util.List;

public interface TripService {
    ResponseObject<?> getAll(Integer routeId, String startTime, String status, String adminCheck, int pageSize, int pageIndex);
    ResponseObject<?> getTripAdminCheck(int pageSize, int pageIndex);

    ResponseObject<?>  getDetail(int id);

    ResponseObject<?> search(String codeDeparturePoint, String codeDestination, String startTime,  int pageSize, int pageIndex);
    ResponseObject<?> searchSeatInTrip(int idStationPickUp, int idStationDropOff, int idTrip, List<ComboSeatStation> comboSeatStations);

    ResponseObject<?>  getDetailAndBookingStatusCHECKIN(int idTrip);

    ResponseObject<?> getHistoryOfDriver(Integer driverId, String status, String startTime, int pageSize, int pageIndex);

    ResponseObject<?> getTripFinishCancelOfDriver(Integer driverId, int pageSize, int pageIndex);

    ResponseObject<?> getTripReadyOfDriver(Integer driverId, int pageSize, int pageIndex);

    ResponseObject<?> getHistoryOfStaff(Integer idStaff,String adminCheck, int pageSize, int pageIndex);

    ResponseObject<?>  create(TripDTOcreate b) throws Exception;

    ResponseObject<?>  update(TripDTOupdateDriverAndBus b);

    ResponseObject<?> startTrip(int idTrip);

    ResponseObject<?>  updateConfirmByDriver(TripConfirm b);

    ResponseObject<?>  updateAcceptTrip(TripAccept b);

    ResponseObject<?> cancelTrip(int idTrip) throws Exception;

    ResponseObject<?> delete(int id) throws Exception;
    ResponseObject<?> notiDriverAfterTripAcceptedByAdmin(int idTrip);
}

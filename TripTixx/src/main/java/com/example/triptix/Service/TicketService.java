package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Station.StationDTOchangeStatus;
import com.example.triptix.DTO.Station.StationDTOcreate;
import com.example.triptix.DTO.Station.StationDTOupdate;
import com.example.triptix.DTO.Ticket.TicketDTOCancelupdate;
import com.example.triptix.DTO.Ticket.TicketDTOChangeSeat;
import com.example.triptix.DTO.Ticket.TicketDTOVoteStartupdate;

public interface TicketService {
    ResponseObject<?> getAll(Integer idTrip, Integer idBooking, String status, Integer idCustomer,  int pageSize, int pageIndex);
    ResponseObject<?>  getDetail(int id);

    ResponseObject<?>  updateVoteStar(TicketDTOVoteStartupdate b);

    ResponseObject<?>  cancelTicket(TicketDTOCancelupdate b);

    ResponseObject<?> getRevenueNgay();

    ResponseObject<?> getRevenueChartQuy(int year);

    ResponseObject<?> getRevenueChart(int year) ;

    ResponseObject<?> getListOfPotentialCustomers() ;

    ResponseObject<?> checkIn(Integer idTrip, String ticketCode, Integer idStationNow) ;

    ResponseObject<?> checkOut(Integer idTrip, String ticketCode);

    ResponseObject<?> changeSeatOfTicket(TicketDTOChangeSeat b);


}

package com.example.triptix.Service;

import com.example.triptix.DTO.Booking.*;
import com.example.triptix.DTO.ResponseObject;

public interface BookingService {
    ResponseObject<?> getAll(Integer idTrip, Integer idCustomer);
    ResponseObject<?>  getDetail(int id);

  
    ResponseObject<?>  create(BookingDTOcreateMore b);
  
//     ResponseObject<?>  create(BookingDTOcreate b);
//    ResponseObject<?>  createBookingRound(BookingDTOcreateRound b) throws Exception;
  
    ResponseObject<?>  createGuest(BookingDTOcreateGuest b);

    ResponseObject<?>  getTicketTypeForCreate(Integer idTrip, Integer codePickUpPoint, Integer codeDropOffPoint);



/*    ResponseObject<?>  update(BookingDTOupdate b);*/

    /*ResponseObject<?> delete(int id);*/

    /*ResponseObject<?> updateVoteStar(BookingDTOupdate b);*/
}

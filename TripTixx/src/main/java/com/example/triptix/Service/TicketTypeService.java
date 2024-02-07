package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.TicketType.TicketTypeCreateDTO;
import com.example.triptix.DTO.TicketType.TicketTypeUpdateDTO;

public interface TicketTypeService {
    ResponseObject<?> getAll();
    ResponseObject<?> getDetail(int id);

/*    ResponseObject<?> create(TicketTypeCreateDTO b);
    ResponseObject<?> update(TicketTypeUpdateDTO b);*/
    ResponseObject<?> delete(int id);
}

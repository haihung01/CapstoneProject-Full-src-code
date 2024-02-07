package com.example.triptix.Service;


import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.SpecialDay.SpecialDayDTOcreate;
import com.example.triptix.DTO.SpecialDay.SpecialDayDTOupdate;

public interface SpecialDayService {
    ResponseObject<?> getAll(int pageSize, int pageIndex);
    ResponseObject<?>  getDetail(int id);

    ResponseObject<?>  create(SpecialDayDTOcreate b);
    ResponseObject<?>  update(SpecialDayDTOupdate b);
    ResponseObject<?>  delete(int id);
    ResponseObject<?>  notiSpecialDay(String date);
}

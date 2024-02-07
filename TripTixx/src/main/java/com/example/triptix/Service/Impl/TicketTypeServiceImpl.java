package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.TicketType.TicketTypeCreateDTO;
import com.example.triptix.DTO.TicketType.TicketTypeUpdateDTO;
import com.example.triptix.DTO.TicketType.TicketTypeViewDTO;
import com.example.triptix.Enum.Status;
import com.example.triptix.Model.Route;
import com.example.triptix.Model.TicketType;
import com.example.triptix.Model.Vehicle;
import com.example.triptix.Repository.RouteRepo;
import com.example.triptix.Repository.TicketTypeRepo;
import com.example.triptix.Service.TicketTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TicketTypeRepo ticketTypeRepo;

    @Autowired
    private RouteRepo routeRepo;
    @Override
    public ResponseObject<?> getAll() {
        try {
            List<TicketType> listTicketType = ticketTypeRepo.findAll();
            List<TicketTypeViewDTO> list = new ArrayList<>();
            for(TicketType ticketType : listTicketType){
                TicketTypeViewDTO dto = modelMapper.map(ticketType, TicketTypeViewDTO.class);
                dto.setIdTicketType(ticketType.getIdTicketType());
                dto.setIdRoute(ticketType.getRoute().getIdRoute());
                dto.setDefaultPrice(ticketType.getDefaultPrice());
                dto.setIdEarlyOnStation(ticketType.getEarlyOnStation().getIdStation());
                dto.setIdLateOffStation(ticketType.getLateOffStation().getIdStation());
                list.add(dto);
            }
            return ResponseObject.builder().status(true)
                    .message("Lấy tất cả loại vé thành công").data(list).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try {
            TicketType ticketType = ticketTypeRepo.findById(id).orElse(null);
            TicketTypeViewDTO dto = modelMapper.map(ticketType, TicketTypeViewDTO.class);
            dto.setIdTicketType(ticketType.getIdTicketType());
            dto.setIdRoute(ticketType.getRoute().getIdRoute());
            dto.setDefaultPrice(ticketType.getDefaultPrice());
            dto.setIdEarlyOnStation(ticketType.getEarlyOnStation().getIdStation());
            dto.setIdLateOffStation(ticketType.getLateOffStation().getIdStation());
            return ResponseObject.builder().status(true)
                    .message("Lấy giá vé thành công").data(dto).build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }
    }

 /*   @Override
    public ResponseObject<?> create(TicketTypeCreateDTO b) {
        try{
            Route route = routeRepo.findById(b.getIdRoute()).orElse(null);
            if(route == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy tuyến đường này.").build();
            }else if(route.getStatus().equals(Status.DEACTIVE.name())){
                return ResponseObject.builder().status(false).message("Tuyến đường này ngưng hoạt động.").build();
            }
            return ResponseObject.builder().status(true)
                    .message("Tạo thành công").data(null).build();
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }
    }

    @Override
    public ResponseObject<?> update(TicketTypeUpdateDTO b) {
        return null;
    }*/

    @Override
    public ResponseObject<?> delete(int id) {
        try {
            //check xem id có obj ko
            TicketType type = ticketTypeRepo.findById(id).orElse(null);
            if(type == null){
                return ResponseObject.builder().status(false).message("Không tìm thấy id ticket type.").build();
            }
            ticketTypeRepo.deleteById(id);
            return ResponseObject.<String>builder().status(true).message("Xóa thành công ticket.").build();
        } catch (Exception ex) {
            return ResponseObject.builder().status(false).message("Có lỗi xảy ra.").build();
        }
    }
}

package com.example.triptix.Controller;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.TicketTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket-types")

public class TicketTypeController {
    @Autowired
    private TicketTypeService ticketTypeService;
    @GetMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getAll(){
        ResponseObject<?> rs = ticketTypeService.getAll();
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/detail")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getDetail(int id){
        ResponseObject<?> rs = ticketTypeService.getDetail(id);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @DeleteMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> delete(int id){
        ResponseObject<?> rs = ticketTypeService.delete(id);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
}

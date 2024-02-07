package com.example.triptix.Controller;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Ticket.TicketDTOCancelupdate;
import com.example.triptix.DTO.Ticket.TicketDTOChangeSeat;
import com.example.triptix.DTO.Ticket.TicketDTOVoteStartupdate;
import com.example.triptix.Service.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer idTrip,
                                    @RequestParam(required = false) Integer idBooking,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) Integer idCustomer,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = ticketService.getAll(idTrip,idBooking,status,idCustomer, pageSize, pageIndex);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/detail")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getDetail(@RequestParam @Valid int id){
        ResponseObject<?> rs = ticketService.getDetail(id);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
    @GetMapping("/revenue-chart-month")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getRevenueChart(@RequestParam @Valid int year){
        ResponseObject<?> rs = ticketService.getRevenueChart(year);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/revenue-chart-quy")
    @SecurityRequirement(name = "BTBsSecurityScheme")

    public ResponseEntity<?> getRevenueChartQuy(@RequestParam int year){
        ResponseObject<?> rs = ticketService.getRevenueChartQuy(year);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/revenue-today")
    @SecurityRequirement(name = "BTBsSecurityScheme")

    public ResponseEntity<?> getRevenueToday(){
        ResponseObject<?> rs = ticketService.getRevenueNgay();
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/top-10-list-of-highest-revenue-trips")
    @SecurityRequirement(name = "BTBsSecurityScheme")

    public ResponseEntity<?> getListOfPotentialCustomers(){
        ResponseObject<?> rs = ticketService.getListOfPotentialCustomers();
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping("/check-in-by-driver")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> checkIn(@RequestParam Integer idTrip , @RequestParam String ticketCode, @RequestParam Integer idStationNow){
        ResponseObject<?> rs = ticketService.checkIn(idTrip, ticketCode,idStationNow );
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
    @PutMapping("/check-out-by-driver")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> checkOut(@RequestParam Integer idTrip , @RequestParam String ticketCode){
        ResponseObject<?> rs = ticketService.checkOut(idTrip, ticketCode);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping("/change-seat-of-ticket")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> changeSeatOfTicket(@RequestBody TicketDTOChangeSeat b){
        ResponseObject<?> rs = ticketService.changeSeatOfTicket(b);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
    @PutMapping("/vote-star-for-customers")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> updateVoteStar(@RequestBody @Valid TicketDTOVoteStartupdate obj){
        ResponseObject<?> rs = ticketService.updateVoteStar(obj);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping("/cancel-ticket-for-customers")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> cancelTicket(@RequestBody @Valid TicketDTOCancelupdate obj){
        ResponseObject<?> rs = ticketService.cancelTicket(obj);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseObject<?> handleValidationExceptions(MethodArgumentNotValidException ex) { //hàm bắt lỗi valid data
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseObject.builder().status(false).code(400).message("lỗi dữ liệu hợp lệ").data(errors).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RollbackException.class)
    public ResponseObject<?> handleRollbackExceptions(RollbackException ex) { //hàm bắt lỗi rollback
        return ResponseObject.builder().status(false).code(400).message("lỗi thực thi").data(ex.getMessage()).build();
    }
}

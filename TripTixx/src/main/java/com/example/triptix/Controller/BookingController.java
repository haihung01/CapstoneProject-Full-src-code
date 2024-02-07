package com.example.triptix.Controller;

import com.example.triptix.DTO.Booking.*;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.BookingService;
import com.example.triptix.Service.MailService;
import com.example.triptix.Service.RedisService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/booking")
@Slf4j
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;


    @GetMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer idTrip, @RequestParam(required = false) Integer idCustomer){
        ResponseObject<?>rs = bookingService.getAll(idTrip,idCustomer);
            if(rs.isStatus()){
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);

    }
    @GetMapping("/detail")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getDetail(@RequestParam @Valid int id){
        ResponseObject<?>rs = bookingService.getDetail(id);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);

    }

    @GetMapping("get-tick-type-of-trip")
//    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getTicketTypeOfTripForCustomer(@RequestParam Integer idTrip, @RequestParam Integer codePickUpPoint, @RequestParam Integer codeDropOffPoint){
        ResponseObject<?>rs = bookingService.getTicketTypeForCreate(idTrip,codePickUpPoint,codeDropOffPoint);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);

    }

    @PostMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> create(@RequestBody @Valid BookingDTOcreateMore b){
        ResponseObject<?> rs = null;
        ResponseObject<?> rsSendMail = null;
        try {
            for (TicketCreate ticket : b.getListTicket()) {
                if(!redisService.antiBookedSameTime(ticket.getSeatName(), b.getIdTrip())){
                    throw new Exception("Hãy thử lại, vé của bạn đã được đặt bởi một khách hàng khác");
                }
            }

            rs = bookingService.create(b);
            if(rs.isStatus()){
                //reset cache
                for (TicketCreate ticket : b.getListTicket()) {
                    redisService.deleteAntiBookedSameTime(ticket.getSeatName(), b.getIdTrip());
                }

                rs.setCode(200);
                BookingResult rb = (BookingResult) rs.getData();
                rsSendMail = mailService.sendMailBookingSuccessTwo(rb.getData().getToEmail(), rb.getData().getNameRoute(), rb.getData().getStartTime(), rb.getData().getEndTime(), rb.getData().getTotalPrice());
                if(!rsSendMail.isStatus()){
                    log.error("Send mail booking error when customer booking: " + rsSendMail.getMessage());
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseObject.builder().status(true).message(rs.getMessage())
                                .data(rb.getMsg()).build());
            }
            //reset cache
            for (TicketCreate ticket : b.getListTicket()) {
                redisService.deleteAntiBookedSameTime(ticket.getSeatName(), b.getIdTrip());
            }

            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (Exception e) {
            //reset cache
            for (TicketCreate ticket : b.getListTicket()) {
                redisService.deleteAntiBookedSameTime(ticket.getSeatName(), b.getIdTrip());
            }

            rs = new ResponseObject<>();
            rs.setCode(400);
            rs.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

//    @PostMapping("/round-trip")
//    @SecurityRequirement(name = "BTBsSecurityScheme")
//    public ResponseEntity<?> create2(@RequestBody @Valid BookingDTOcreateRound b){
//        ResponseObject<?> rs = null;
//        try {
//            if(!redisService.antiBookedSameTime(b.getSeatName(), b.getIdTrip())){
//                throw new Exception("Hãy thử lại, vé của bạn đã được đặt bởi một khách hàng khác");
//            }
//            if(!redisService.antiBookedSameTime(b.getSeatName2(), b.getIdTrip2())){
//                throw new Exception("Hãy thử lại, vé của bạn đã được đặt bởi một khách hàng khác");
//            }
//            rs = bookingService.createBookingRound(b);
//            if(rs.isStatus()){
//                //reset cache
//                redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());
//                redisService.deleteAntiBookedSameTime(b.getSeatName2(), b.getIdTrip2());
//
//                rs.setCode(200);
//                return ResponseEntity.status(HttpStatus.OK).body(rs);
//            }
//            //reset cache
//            /*redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());*/
////             redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());
////             redisService.deleteAntiBookedSameTime(b.getSeatName2(), b.getIdTrip2());
//
//            rs.setCode(400);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
//        } catch (Exception e) {
//            //reset cache
//            /*redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());*/
////             redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());
////             redisService.deleteAntiBookedSameTime(b.getSeatName2(), b.getIdTrip2());
//
//            rs = new ResponseObject<>();
//            rs.setCode(400);
//            rs.setMessage(e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
//        }
//    }

    @PostMapping("/guest")
    public ResponseEntity<?> createGuest(@RequestBody @Valid BookingDTOcreateGuest b){
        ResponseObject<?> rs = null;
        try {
            if(!redisService.antiBookedSameTime(b.getSeatName(), b.getIdTrip())){
                throw new Exception("Hãy thử lại, vé của bạn đã được đặt bởi một khách hàng khác");
            }
            rs = bookingService.createGuest(b);
            if(rs.isStatus()){
                //reset cache
                redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());

                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            //reset cache
            redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());

            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (Exception e) {
            //reset cache
            redisService.deleteAntiBookedSameTime(b.getSeatName(), b.getIdTrip());

            rs = new ResponseObject<>();
            rs.setCode(400);
            rs.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
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

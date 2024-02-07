package com.example.triptix.Controller;

import com.example.triptix.DTO.Payment.GuestBookingDTO;
import com.example.triptix.DTO.Payment.PaymentInput;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.PaymentService;
import com.example.triptix.Util.VnPayHelper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
//@SecurityRequirement(name = "BTBsSecurityScheme")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @GetMapping("/create_payment")
    public ResponseEntity<?> createPayment(HttpServletRequest req,
                                           @RequestParam @Min(10000) int amount,
                                           @RequestParam int idCustomer) {
        ResponseObject<?> rs = service.createPayment(req, amount, idCustomer);
        if (rs.isStatus()) {
//            return ResponseEntity.status(HttpStatus.OK).body(rs);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, (String) rs.getData());
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/booking-guest")
    @Operation(description = "name Guest khuyến khích Guest khogn6 nên nhập dấu, NHẬP TÊN KHÔNG DẤU")
    public ResponseEntity<?> bookingGuest3(HttpServletRequest req, GuestBookingDTO guestBookingDTO) {
        ResponseObject<?> rs = service.bookingGuest(req, guestBookingDTO);
        if (rs.isStatus()) {
//            return ResponseEntity.status(HttpStatus.OK).body(rs);
            System.out.println("URl guest booking: " + rs.getData());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, (String) rs.getData());
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping("/booking-guest")
    @Operation(description = "name Guest khuyến khích Guest khogn6 nên nhập dấu, NHẬP TÊN KHÔNG DẤU")
    public ResponseEntity<?> bookingGuest(HttpServletRequest req,
                                          @RequestBody @Valid GuestBookingDTO guestBookingDTO) {
        ResponseObject<?> rs = service.bookingGuest(req, guestBookingDTO);
        if (rs.isStatus()) {
//            return ResponseEntity.status(HttpStatus.OK).body(rs);
            System.out.println("URl guest booking: " + rs.getData());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, (String) rs.getData());
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping("/booking-guest-url")
    public ResponseEntity<?> bookingGuest2(HttpServletRequest req,
                                          @RequestBody @Valid GuestBookingDTO guestBookingDTO) {
        ResponseObject<?> rs = service.bookingGuest(req, guestBookingDTO);
        if (rs.isStatus()) {
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping("/create_payment-url")
    public ResponseEntity<?> createPaymentURL(HttpServletRequest req,
                                              @RequestBody @Valid PaymentInput paymentInput) {
        ResponseObject<?> rs = service.createPayment(req, paymentInput.getAmount(), paymentInput.getIdCustomer());
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/payment_result")
    public ResponseEntity<?> transaction(   //hàm bắt kq giao dịch (transaction - success, fail, hủy thanh toán, ...) về từ VNPAY
                                            @RequestParam(value = "vnp_Amount") String vnp_Amount,
                                            @RequestParam(value = "vnp_BankCode") String vnp_BankCode,
                                            @RequestParam(value = "vnp_OrderInfo") String vnp_OrderInfo,
                                            @RequestParam(value = "vnp_PayDate") String vnp_PayDate,
                                            @RequestParam(value = "vnp_ResponseCode") String vnp_ResponseCode) {
        ResponseObject<?> rs = service.resultTransaction(vnp_Amount, vnp_BankCode, vnp_OrderInfo, vnp_PayDate, vnp_ResponseCode);
        HttpHeaders headers = new HttpHeaders();
        //test
        System.out.println(rs);
        //test
        if (rs.isStatus()) {
            rs.setCode(200);
//            return ResponseEntity.status(HttpStatus.OK).body(rs);
            if(rs.getMessage().contains("guest")){
                headers.add(HttpHeaders.LOCATION, VnPayHelper.vnp_RedirectResultGuestBooking_success);
            }else{
                headers.add(HttpHeaders.LOCATION, VnPayHelper.vnp_RedirectResult + "?status=success");
            }
            //redirect qua URl khác
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }else{
            rs.setCode(400);
            if(rs.getMessage().contains("guest")){
                headers.add(HttpHeaders.LOCATION, VnPayHelper.vnp_RedirectResultGuestBooking_fail);
            }else{
                headers.add(HttpHeaders.LOCATION, VnPayHelper.vnp_RedirectResult + "?status=fail");
            }
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

    @GetMapping("/result")
    public ResponseEntity<?> result(@RequestParam(required = false) String status){
        if(status == null){
            status = "pending";
        }
        ResponseObject<?> rs = service.resultPayment(status);
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
}

package com.example.triptix.Controller;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.MailService;
import com.example.triptix.Service.RedisService;
import com.example.triptix.Service.SMSService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/otp")
//@SecurityRequirement(name = "BTBsSecurityScheme")
public class OTPController {
    @Autowired
    private SMSService smsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;
    @PostMapping("/phone/send")
    public ResponseEntity<?> sendPhoneOTP(@RequestParam @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits") String phone) {
        ResponseObject<?> rs = smsService.sendOTP(phone);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping("/email/send")
    public ResponseEntity<?> sendMailOTP(@RequestParam String email) {
        ResponseObject<?> rs = mailService.sendMailOTP(email);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/valid")
    public ResponseEntity<?> transaction(@Parameter(description = "Email or Phone") @RequestParam String key,
                                         @RequestParam int otp) {
        ResponseObject<?> rs = redisService.checkOTP(key, otp);
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

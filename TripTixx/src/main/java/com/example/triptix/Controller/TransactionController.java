package com.example.triptix.Controller;

import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment-transaction")
@SecurityRequirement(name = "BTBsSecurityScheme")
public class TransactionController {
    @Autowired
    private TransactionService service;
    @GetMapping("")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_CUSTOMER, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> getAllOfCustomer(@RequestParam int isCustomer,
                                              @RequestParam(required = false) Integer pageSize,
                                              @RequestParam(required = false) Integer pageIndex){
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = service.getAllOfCustomer(isCustomer, pageSize, pageIndex);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/detail")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_CUSTOMER, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> detail(@RequestParam int idPaymentTransaction) {
        ResponseObject<?> rs = service.getDetail(idPaymentTransaction);
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

package com.example.triptix.Controller;

import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.SpecialDay.SpecialDayDTOcreate;
import com.example.triptix.DTO.SpecialDay.SpecialDayDTOupdate;
import com.example.triptix.Service.SpecialDayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/special-day")
@SecurityRequirement(name = "BTBsSecurityScheme")
public class SpecialDayController {
    @Autowired
    private SpecialDayService specialDayService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex){
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = specialDayService.getAll(pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam int id){
        ResponseObject<?> rs = specialDayService.getDetail(id);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @PostMapping
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> create(@RequestBody @Valid SpecialDayDTOcreate objDTO) {
        ResponseObject<?> rs = specialDayService.create(objDTO);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> update(@RequestBody @Valid SpecialDayDTOupdate objDTO) {
        ResponseObject<?> rs = specialDayService.update(objDTO);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @DeleteMapping
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> delete(@RequestParam int id){
        ResponseObject<?> rs = specialDayService.delete(id);
        if(rs.isStatus()){
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
    @ExceptionHandler(BindException.class)
    public ResponseObject<?> handleRollbackExceptions(BindException ex) { //hàm bắt lỗi binding data
        return ResponseObject.builder().status(false).code(400).message("lỗi thực thi").data(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RollbackException.class)
    public ResponseObject<?> handleRollbackExceptions(RollbackException ex) { //hàm bắt lỗi rollback
        return ResponseObject.builder().status(false).code(400).message("lỗi thực thi").data(ex.getMessage()).build();
    }
}

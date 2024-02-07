package com.example.triptix.Controller;


import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Route.RouteDTOchangeStatus;
import com.example.triptix.DTO.Route.RouteDTOcreate;
import com.example.triptix.DTO.Route.RouteDTOupdate;
import com.example.triptix.Service.RouteService;
import com.example.triptix.Service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @GetMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getAll(@RequestParam(required = false) String codeDepartureDate,
                                    @RequestParam(required = false) String codeEndDate, @RequestParam(required = false) String name){
        ResponseObject<?> rs = routeService.getAll(codeDepartureDate,codeEndDate,name);
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
        ResponseObject<?> rs = routeService.getDetail(id);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }




    @PostMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    @Operation(description = "Thuộc tính: orderInRoute là số thứ tự của lịch trình trạm. Không cần nhập, nó tự tăng")
    public ResponseEntity<?> create(@RequestBody @Valid RouteDTOcreate obj){
        ResponseObject<?> rs = routeService.create(obj);
        if (rs.isStatus()) {
            rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
    @PutMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> update(@RequestBody @Valid RouteDTOupdate obj){
        ResponseObject<?> rs = routeService.update(obj);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }


    @PutMapping("/change-status")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> delete(@RequestBody @Valid RouteDTOchangeStatus obj){
        ResponseObject<?> rs = routeService.updateStatus(obj);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @DeleteMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> delete(@RequestParam @Valid int id){
        ResponseObject<?> rs = routeService.delete(id);
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

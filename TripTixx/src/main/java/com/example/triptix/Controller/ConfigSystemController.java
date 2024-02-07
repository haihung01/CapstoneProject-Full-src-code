package com.example.triptix.Controller;


import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.ConfigSystem.ConfigSystemDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.ConfigSystem;
import com.example.triptix.Service.ConfigSystemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config-system")
@SecurityRequirement(name = "BTBsSecurityScheme")
public class ConfigSystemController {
    @Autowired
    private ConfigSystemService configSystemService;
    @GetMapping
    public ResponseEntity<?> getAll(){
        ResponseObject<?> rs = configSystemService.getAll();
        if(!rs.isStatus()){
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam int id){
        ResponseObject<?> rs = configSystemService.getDetail(id);
        if(!rs.isStatus()){
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }
    @PostMapping
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> create(@RequestBody @Valid ConfigSystemDTOcreate configSystemDTOcreate){
        ResponseObject<?> rs = configSystemService.create(configSystemDTOcreate);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> update(@RequestBody @Valid ConfigSystem configSystem){
        ResponseObject<?> rs = configSystemService.update(configSystem);
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
        ResponseObject<?> rs = configSystemService.delete(id);
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
}

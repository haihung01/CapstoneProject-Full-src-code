package com.example.triptix.Controller;

import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.ProvinceCity.ProvinceCityDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.ProvinceCityService;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/province-city")
public class ProvinceCityController {
    @Autowired
    private ProvinceCityService provinceCityService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex,
                                    @Parameter(description = "CITY/ PROVINCE/ VN") @RequestParam(required = false) String type){
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = provinceCityService.getAll(type, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @PostMapping()
    public ResponseEntity<?> add(@RequestBody @Valid ProvinceCityDTOcreate provinceCityDTOcreate){
        ResponseObject<?> rs = provinceCityService.add(provinceCityDTOcreate);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @DeleteMapping()
    public ResponseEntity<?> delete(@RequestParam String id){
        ResponseObject<?> rs = provinceCityService.delete(id);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam String id){
        ResponseObject<?> rs = provinceCityService.getDetail(id);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @GetMapping("/import")
    public ResponseEntity<?> importData(){
        ResponseObject<?> rs = provinceCityService.importData();
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAll(){
        ResponseObject<?> rs = provinceCityService.deleteAll();
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
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

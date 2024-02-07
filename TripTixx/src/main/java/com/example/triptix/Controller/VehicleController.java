package com.example.triptix.Controller;

import com.example.triptix.DTO.News.UpdateObjectImg;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Vehicle.VehicleDTOchangeStatus;
import com.example.triptix.DTO.Vehicle.VehicleDTOcreate;
import com.example.triptix.DTO.Vehicle.VehicleDTOupdate;
import com.example.triptix.Service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getAll(@RequestParam(required = false) String type,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex){
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = vehicleService.getAll(type, pageSize, pageIndex);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/detail")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getDetail(@RequestParam int id){
        ResponseObject<?> rs = vehicleService.getDetail(id);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping(path = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,     //này là dể nó cho phép swagger upload file
            produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> create(@ModelAttribute @Valid VehicleDTOcreate obj, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            // Bắt lỗi từng lỗi và trả về Map với key là tên thuộc tính sai và value là nội dung lỗi
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            // Trả về Map lỗi
            ResponseObject<?> rs = ResponseObject.builder().status(false).message("lỗi dữ liệu hợp lệ").data(errorMap).build();
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }

        ResponseObject<?> rs = vehicleService.create(obj);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
    @PutMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    @Operation(description = "Không cho update biển số vì biển số là duy nhất.")
    public ResponseEntity<?> update(@RequestBody @Valid VehicleDTOupdate obj){
        ResponseObject<?> rs = vehicleService.update(obj);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping(path = "/img",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,     //này là dể nó cho phép swagger upload file
            produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> updateImgNews(@ModelAttribute @Valid UpdateObjectImg obj, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Bắt lỗi từng lỗi và trả về Map với key là tên thuộc tính sai và value là nội dung lỗi
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            // Trả về Map lỗi
            ResponseObject<?> rs = ResponseObject.builder().status(false).message("lỗi dữ liệu hợp lệ").data(errorMap).build();
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
        ResponseObject<?> rs = vehicleService.updateImgNew(obj.getIdObj(), obj.getImg());
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping("/change-status")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> changeStatus(@RequestBody @Valid VehicleDTOchangeStatus changeStatusBus){
        ResponseObject<?> rs = vehicleService.updateStatus(changeStatusBus);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @DeleteMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> delete(@RequestParam int id){
        ResponseObject<?> rs = vehicleService.delete(id);
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
    @ExceptionHandler(RollbackException.class)
    public ResponseObject<?> handleRollbackExceptions(RollbackException ex) { //hàm bắt lỗi rollback
        return ResponseObject.builder().status(false).code(400).message("lỗi thực thi").data(ex.getMessage()).build();
    }
}

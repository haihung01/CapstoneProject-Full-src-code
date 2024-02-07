package com.example.triptix.Controller;

import com.example.triptix.DTO.News.NewsDTOcreate;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Station.StationDTOchangeStatus;
import com.example.triptix.DTO.Station.StationDTOcreate;
import com.example.triptix.DTO.Station.StationDTOupdate;
import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.Service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stations")
public class StationController {
    @Autowired
    private StationService stationService;

    @GetMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> getAll(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String province,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex){
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = stationService.getAll(name, province, pageSize, pageIndex);
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
        ResponseObject<?> rs = stationService.getDetail(id);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }



    @PostMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> create(@RequestBody @Valid StationDTOcreate obj){
        ResponseObject<?> rs = stationService.create(obj);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PutMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> update(@RequestBody @Valid StationDTOupdate stationDTOupdate){
        ResponseObject<?> rs = stationService.update(stationDTOupdate);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @PutMapping("/change-status")
    @Operation(description = "truyền trạng thái cho xe chỉ có DEACTIVE và ACTIVE")
    public ResponseEntity<?> updateStatus(@RequestBody @Valid StationDTOchangeStatus obj){
        ResponseObject<?> rs = stationService.updateStatus(obj);
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
        ResponseObject<?> rs = stationService.delete(id);
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

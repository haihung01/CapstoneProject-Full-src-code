package com.example.triptix.Controller;

import com.example.triptix.DTO.Trip.*;
import com.example.triptix.Enum.AdminCheck;
import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.TripService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trips")
public class TripController {
    @Autowired
    private TripService tripService;

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer routeId,
                                    @RequestParam(required = false) String startTime,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String adminCheck,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.getAll(routeId, startTime, status, adminCheck, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @GetMapping("/trip-admin-check")
    public ResponseEntity<?> getTripAdminCheck(
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.getTripAdminCheck(pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getAll(@RequestParam(required = false) String codeDeparturePoint,
                                    @RequestParam(required = false) String codeDestination,
                                    @RequestParam(required = false) String startTime,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.search(codeDeparturePoint, codeDestination, startTime, pageSize, pageIndex);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        } else {
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

    @PutMapping("/find-seat")
    public ResponseEntity<?> findSeat(@RequestBody FindSeatObject findSeatObject) {
        ResponseObject<?> rs = null;
        try {
            rs = tripService.searchSeatInTrip(findSeatObject.getIdStationPickUp(), findSeatObject.getIdStationDropOff(), findSeatObject.getIdTrip(), findSeatObject.getComboSeatStations());
            if(rs.isStatus()){
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam int id) {
        ResponseObject<?> rs = tripService.getDetail(id);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @GetMapping("/list-customer-check-in-of-trip")
    public ResponseEntity<?> getDetailAndBookingStatusCheckIn(@RequestParam int idTrip) {
        ResponseObject<?> rs = tripService.getDetailAndBookingStatusCHECKIN(idTrip);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @GetMapping("/history-driver")
    public ResponseEntity<?> getHistoryOfDriver(@RequestParam Integer driverId,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(required = false) String startTime,
                                                @RequestParam(required = false) Integer pageSize,
                                                @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.getHistoryOfDriver(driverId, status, startTime, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @GetMapping("/trip-finish-cancel-of-driver")
    public ResponseEntity<?> getHistoryOfDriver2(@RequestParam Integer driverId,
                                                 @RequestParam(required = false) Integer pageSize,
                                                 @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.getTripFinishCancelOfDriver(driverId, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @GetMapping("/trip-ready-of-driver")
    public ResponseEntity<?> getHistoryOfDriver3(@RequestParam Integer driverId,
                                                 @RequestParam(required = false) Integer pageSize,
                                                 @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.getTripReadyOfDriver(driverId, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @GetMapping("/history-staff")
    public ResponseEntity<?> getHistoryOfStaff(@RequestParam Integer staffId,
                                               @RequestParam(required = false) String adminCheck,
                                               @RequestParam(required = false) Integer pageSize,
                                               @RequestParam(required = false) Integer pageIndex) {
        if (pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = tripService.getHistoryOfStaff(staffId, adminCheck, pageSize, pageIndex);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> create(@RequestBody @Valid TripDTOcreate b){
        ResponseObject<?> rs = null;
        try {
            rs = tripService.create(b);
            if(rs.isStatus()){
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (Exception e) {
            rs = new ResponseObject<>();
            rs.setCode(400);
            rs.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @PutMapping
    public ResponseEntity<?> update(@RequestBody @Valid TripDTOupdateDriverAndBus tripDTOupdate) {
        ResponseObject<?> rs = new ResponseObject<>();
        try {
            rs = tripService.update(tripDTOupdate);
            if(rs.isStatus()){
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (Exception ex) {
            rs.setCode(400);
            rs.setStatus(false);
            rs.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @PutMapping("/start-trip-by-driver")
    public ResponseEntity<?> startTrip(@RequestParam int idTrip) {
        ResponseObject<?> rs = tripService.startTrip(idTrip);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @PutMapping("/confirm-finish-trip-by-driver")
    public ResponseEntity<?> updateConfirmByDriver(@RequestBody @Valid TripConfirm tripConfirm) {
        ResponseObject<?> rs = tripService.updateConfirmByDriver(tripConfirm);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @PutMapping("/accept-trip-by-admin")
    public ResponseEntity<?> updateAcceptTripByAdmin(@RequestBody @Valid TripAccept tripAccept) {
        ResponseObject<?> rs = tripService.updateAcceptTrip(tripAccept);
        if (rs.isStatus()) {
            if(tripAccept.getAdminCheck().equals(AdminCheck.ACCEPTED.name())){
                rs = tripService.notiDriverAfterTripAcceptedByAdmin(tripAccept.getIdTrip());
                if (rs.isStatus()) {
                    rs.setCode(200);
                    return ResponseEntity.status(HttpStatus.OK).body(rs);
                }
            }else{
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @PutMapping("/cancel-trip")
    public ResponseEntity<?> cancelTrip(@RequestParam int idTrip) {
        ResponseObject<?> rs = null;
        try{
            rs = tripService.cancelTrip(idTrip);
            if (rs.isStatus()) {
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }catch (Exception e){
            rs = ResponseObject.builder().status(false).code(400).message(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam int id) {
        ResponseObject<?> rs = null;
        try {
            rs = tripService.delete(id);
            if (rs.isStatus()) {
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (Exception e) {
            rs = ResponseObject.builder().status(false).code(400).message(e.getMessage()).build();
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

package com.example.triptix.Controller;

import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.UserSystem.*;
import com.example.triptix.Service.UserSystemService;
import com.example.triptix.Util.JwtHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usersystem")
public class UserSystemController {
    @Autowired
    private UserSystemService userSystemService;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginBody objLogin) {
        return jwtHelper.checkLoginToCreateToken(objLogin.getUsername(), objLogin.getPassword());
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @GetMapping
    @Operation(description = "nếu truyền về CUSTOMER/ DRIVER/ STAFF --> lấy list dsach user của từng role  \n" +
            "Nếu bỏ trống --> trả về dsach all role (trừ admin)")
    public ResponseEntity getall(@RequestParam(required = false) String role,
                                 @RequestParam(required = false) Integer pageSize,
                                 @RequestParam(required = false) Integer pageIndex) {
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = userSystemService.getAll(role, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam int id) {
        ResponseObject<?> rs = userSystemService.getDetail(id);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @PostMapping("/register")
    @Operation(description = "Nếu tạo CUSTOMER -> ko cần truyền về belongTo, citizenIdentityCard  \n" +
            "Nếu tạo DRIVER -> truyền hết  \n" +
            "Nếu tạo STAFF ->  ko cần truyền về belongTo ")
    public ResponseEntity<?> create(@RequestBody @Valid UserSystemDTOcreateTimeStamp objDTOcreate) {
        ResponseObject<?> rs = userSystemService.create(objDTOcreate);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @PostMapping("/register-2")
    @Operation(description = "Nếu tạo CUSTOMER -> ko cần truyền về belongTo, citizenIdentityCard  \n" +
            "Nếu tạo DRIVER -> truyền hết  \n" +
            "Nếu tạo STAFF ->  ko cần truyền về belongTo ")
    public ResponseEntity<?> create2(@RequestBody @Valid UserSystemDTOcreate2 objDTOcreate) {
        //parse lấy birthday timestamp
        ModelMapper modelMapper = new ModelMapper();
        UserSystemDTOcreateTimeStamp objDTOcreateTimeStamp = modelMapper.map(objDTOcreate, UserSystemDTOcreateTimeStamp.class);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            objDTOcreateTimeStamp.setBirthdayTimeStamp(sdf.parse(objDTOcreate.getBirthday()).getTime() / 1000);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ResponseObject<?> rs = userSystemService.create(objDTOcreateTimeStamp);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @PutMapping
    public ResponseEntity<?> update(@RequestBody @Valid UserSystemDTOupdateTimeStamp objDTOupdate) {
        ResponseObject<?> rs = userSystemService.update(objDTOupdate);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @PutMapping("/update-2")
    public ResponseEntity<?> update2(@RequestBody @Valid UserSystemDTOupdateTimeStamp2 objDTOupdate) {
        //parse lấy birthday timestamp
        ModelMapper modelMapper = new ModelMapper();
        UserSystemDTOupdateTimeStamp objDTOupdateTimeStamp = modelMapper.map(objDTOupdate, UserSystemDTOupdateTimeStamp.class);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ResponseObject<?> rs = new ResponseObject<>();
        try {
            Date date = sdf.parse(objDTOupdate.getSinhnhat());
            Long seconds = date.getTime() / 1000;
            objDTOupdateTimeStamp.setBirthdayTimeStamp(seconds);

            rs = userSystemService.update(objDTOupdateTimeStamp);
            if (rs.isStatus()) {
                rs.setCode(200);
                return ResponseEntity.status(HttpStatus.OK).body(rs);
            }
            rs.setCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            rs.setCode(400);
            rs.setStatus(false);
            rs.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
        }
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam int id) {
        ResponseObject<?> rs = userSystemService.delete(id);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePwd(@RequestBody @Valid ChangePassWordDTO changePassWordDTO) {
        ResponseObject<?> rs = userSystemService.changePassWord(changePassWordDTO.getIdUser(), changePassWordDTO.getOldPassword(), changePassWordDTO.getNewPassword());
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @PutMapping("/de-active-account")
    public ResponseEntity<?> deActiveAccount(@RequestParam int idUser) {
        ResponseObject<?> rs = userSystemService.deActiveAccountUser(idUser);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    @PutMapping("/active-account")
    public ResponseEntity<?> activeAccount(@RequestParam int idUser) {
        ResponseObject<?> rs = userSystemService.activeAccountUser(idUser);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_CUSTOMER})
    @PutMapping("/exchange-voucher-coins")
    public ResponseEntity<?> exchangeVoucherCoin(@RequestBody @Valid ExchangeVoucherCoinDTO objDTOcreate) {
        ResponseObject<?> rs = userSystemService.exchangeVoucherCoin(objDTOcreate.getVoucherCoins(), objDTOcreate.getIdCustomer());
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @SecurityRequirement(name = "BTBsSecurityScheme")
    @RolesAllowed({RoleAutho.ROLE_CUSTOMER, RoleAutho.ROLE_DRIVER, RoleAutho.ROLE_STAFF})
    @PutMapping("/fcm-token-devide")
    public ResponseEntity<?> updateFcmToken(@RequestBody fcmToken fcmToken) {
        ResponseObject<?> rs = userSystemService.updateFcmToken(fcmToken.getFcmTokenDevide(), fcmToken.getIdCustomer());
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

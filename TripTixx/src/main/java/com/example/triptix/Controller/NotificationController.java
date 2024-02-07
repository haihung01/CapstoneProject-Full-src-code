package com.example.triptix.Controller;


import com.example.triptix.Enum.RoleAutho;
import com.example.triptix.DTO.Notification.NotificationDTOcreate;
import com.example.triptix.DTO.Notification.NotificationMessage;
import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Service.Impl.FireBaseNotificationMessagingServiceImpl;
import com.example.triptix.Service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/notification")
@SecurityRequirement(name = "BTBsSecurityScheme")
public class NotificationController {
    @Autowired
    private FireBaseNotificationMessagingServiceImpl firebaseMessagingService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> sendNotification(@RequestBody NotificationMessage notificationMessage) {
        ResponseObject<?> rs = firebaseMessagingService.sendNotification(notificationMessage);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/send-birthday")
    @RolesAllowed({RoleAutho.ROLE_ADMIN, RoleAutho.ROLE_STAFF})
    public ResponseEntity<?> sendNotificationToUser(@RequestParam String fcmTokenCustomer) {
        ResponseObject<?> rs = firebaseMessagingService.notiBirthday(fcmTokenCustomer);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) Integer pageIndex,
                                     @RequestParam(required = false) Integer idUser) {
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = notificationService.getAll(idUser, pageSize, pageIndex);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam int id){
        ResponseObject<?> rs = notificationService.getDetail(id);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @PostMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> create(@RequestBody @Valid NotificationDTOcreate notificationDTOcreate){
        ResponseObject<?> rs = notificationService.create(notificationDTOcreate);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }


    @PutMapping("/seen")
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> update(@RequestParam int idUser){
        ResponseObject<?> rs = notificationService.updateSeen(idUser);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @DeleteMapping
    @SecurityRequirement(name = "BTBsSecurityScheme")
    public ResponseEntity<?> delete(@RequestParam int idNotification){
        ResponseObject<?> rs = notificationService.delete(idNotification);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
}

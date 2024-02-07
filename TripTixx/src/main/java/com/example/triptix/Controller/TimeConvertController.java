package com.example.triptix.Controller;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Repository.TripRepo;
import com.example.triptix.Util.AesEncryptionUtil;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/time-convert")
//@SecurityRequirement(name = "BTBsSecurityScheme")
public class TimeConvertController {

    @GetMapping("/convert-time-format-to-long-seconds")
    public ResponseEntity<?> convertTimeFormatToLongSeconds(@Parameter(description = "must format: dd-MM-yyyy or dd-MM-yyyy HH:mm:ss") String dateStr) {
        ResponseObject<?> rs = null;
        try {
            SimpleDateFormat sdf = null;
            if (dateStr.length() == 10) {
                sdf = new SimpleDateFormat("dd-MM-yyyy");
            } else {
                sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            }
            Date date = sdf.parse(dateStr);
            Long seconds = date.getTime() / 1000;
            return ResponseEntity.status(HttpStatus.OK).body("Your " + dateStr + " --> " + seconds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail to convert" + e.getMessage());
        }
    }

    @GetMapping("/convert-long-time-format-to-date")
    public ResponseEntity<?> longtimeFormatToDate(@Parameter(description = "seconds") @RequestParam(required = false) Long longTimes) {
        ResponseObject<?> rs = null;
        try {
            if(longTimes == null){
                //lấy time htai5 trên server
                longTimes = System.currentTimeMillis() / 1000;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(longTimes * 1000);
            return ResponseEntity.status(HttpStatus.OK).body("Your " + longTimes + " --> " + sdf.format(date));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail to convert" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String pwdex = "uDEWe3YtoSOQT2rb9jTsPQ==";
        try {
            AesEncryptionUtil aesEncryptionUtil = new AesEncryptionUtil();
            System.out.println(aesEncryptionUtil.decrypt(pwdex));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.triptix.Controller;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.Model.key.StationTimeComeKey;
import com.example.triptix.Service.Impl.CronJob;
import com.example.triptix.Service.StationTimeComeService;
import com.example.triptix.Service.TripService;
import com.example.triptix.Util.AesEncryptionUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/station-timecome")
@SecurityRequirement(name = "BTBsSecurityScheme")
public class StationTimeComeController {

    @Autowired
    private StationTimeComeService stationTimeComeService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer idTrip,
                                    @RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageIndex){
        if(pageSize == null || pageIndex == null) {
            pageSize = 0;
            pageIndex = 0;
        }
        ResponseObject<?> rs = stationTimeComeService.getAll(idTrip, pageSize, pageIndex);
        if (rs.isStatus()) {
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam StationTimeComeKey idStationTimeCome){
        ResponseObject<?> rs = stationTimeComeService.getDetail(idStationTimeCome);
        rs.setCode(200);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam StationTimeComeKey idStationTimeCome){
        ResponseObject<?> rs = stationTimeComeService.delete(idStationTimeCome);
        if(rs.isStatus()){
            rs.setCode(200);
            return ResponseEntity.status(HttpStatus.OK).body(rs);
        }
        rs.setCode(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rs);
    }
}

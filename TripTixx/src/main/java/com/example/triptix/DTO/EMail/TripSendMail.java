package com.example.triptix.DTO.EMail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripSendMail {
    private String departurePoint;
    private String destination;
    private String startTime;
    private String endTime;
    private String stationTimeComeStart;
    private String stationStartAddress;
    private String stationTimeComeEnd;
    private String stationEndAddress;
    private String busLicensePlates;
    private String ticketCode;
}

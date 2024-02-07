package com.example.triptix.DTO.Trip;

import com.example.triptix.Util.ValidData.DateTime.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripDTOcreate {
    @NotNull(message = "Không để trống id tuyến đường")
    private int idRoute;

    @NotNull(message = "Không để trống id xe")
    private int idVehicle;

    @NotNull(message = "Không để trống id nhân viên ")
    private int idStaff;

    @NotNull(message = "Không để trống id tài xế")
    private int idDriver;

//    @NotNull(message = "Không để trống ngày khởi hành")
//    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "Không để trống ngày khởi hành")
    @Pattern(regexp = "^(\\d{2}-\\d{1,2}-\\d{4} \\d{2}:\\d{2}:\\d{2})$", message = "Invalid time format. Please use 'dd-M-yyyy HH:mm:ss'")
    private String departureDate;

//    @NotNull(message = "Không để trống ngày kết thúc")
//    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Pattern(regexp = "^(\\d{4}-\\d{1,2}-\\d{2} \\d{2}:\\d{2}:\\d{2})$", message = "Invalid time format. Please use 'yyyy-MM-dd HH:mm:ss'")
    @NotBlank(message = "Không để trống ngày kết thúc")
    @Pattern(regexp = "^(\\d{2}-\\d{1,2}-\\d{4} \\d{2}:\\d{2}:\\d{2})$", message = "Invalid time format. Please use 'dd-M-yyyy HH:mm:ss'")
    private String endDate;

    private List<String> listSchedule; //yyyy-MM-dd

    private List<TicketTypeInTripDTOcreate> listTicketTypeInTrip;

    private List<StationTimeComeDTOcreateInTrip> listStationTimeCome;
}

package com.example.triptix.DTO.Route;

import com.example.triptix.Model.TicketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDTOview {

    private int idRoute;

    private String name;

    private String departurePoint;

    private String destination;

    private String status;

    private Date createdDate;

    private Date updatedDate;

    private int idAdmin;

    private List<StationInRouteDTOview> listStationInRoute;

    private List<TicketTypeDTOview> listTicketType;
}

package com.example.triptix.DTO.Route;

import com.example.triptix.Util.ValidData.ValueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDTOcreate {
/*    private int idRoute;*/

    @NotBlank(message = "depaturePoint is required")
    //bắt name tỉnh trong Service Impl
    private String codeDeparturePoint;

    @NotBlank(message = "depaturePoint is required")
    //bắt name tỉnh trong Service Impl
    private String codeDestination;

    @NotBlank(message = "Tên tuyến không được để trống.")
    private String name;

 /*   @Min(value = 0, message = "Tiền của tuyến đường này tối thiểu 0 đồng")
    private double defaultPrice;*/

   /* private int idAdmin;*/
    List<StationInRouteDTOcreate> listStationInRoute;

    List<TicketTypeDTOcreate> listTicketType;

}

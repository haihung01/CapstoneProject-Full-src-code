package com.example.triptix.DTO.Trip;

import com.example.triptix.Util.ValidData.AdminCheck.ValueAdminCheck;
import com.example.triptix.Util.ValidData.StatusTrip.ValueStatusTrip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripAccept {
    private int idTrip;

    @ValueAdminCheck
    private String adminCheck;

    private List<Integer> listIdTripSchedule;
}
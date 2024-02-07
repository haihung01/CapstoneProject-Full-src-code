package com.example.triptix.DTO.Trip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ComboSeatStation {
    private int codePickUpPoint;
    private int codeDropOffPoint;
    private List<String> seatName;
}

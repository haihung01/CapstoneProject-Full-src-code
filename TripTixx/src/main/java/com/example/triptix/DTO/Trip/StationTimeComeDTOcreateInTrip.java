package com.example.triptix.DTO.Trip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationTimeComeDTOcreateInTrip {
    private int idStationInRoute;

    @NotBlank(message = "time is required")
    @Pattern(regexp = "^(\\d{2}:\\d{2}:\\d{2})$", message = "Invalid time format. Please use 'HH:mm:ss'")
    private String timeComes;
}

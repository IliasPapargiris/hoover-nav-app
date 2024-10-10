package com.rationaldata.robotic_hoover.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HooverResponse {

    @NotNull(message = "Final coordinates cannot be null")
    private Coords coords;

    @NotNull(message = "Number of cleaned patches cannot be null")
    private Integer patches;
}

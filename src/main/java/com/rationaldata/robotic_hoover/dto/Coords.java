package com.rationaldata.robotic_hoover.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coords {

    @NotNull(message = "X coordinate cannot be null")
    @Min(value = 0, message = "X coordinate cannot be negative")
    private Integer x;

    @NotNull(message = "Y coordinate cannot be null")
    @Min(value = 0, message = "Y coordinate cannot be negative")
    private Integer y;
}

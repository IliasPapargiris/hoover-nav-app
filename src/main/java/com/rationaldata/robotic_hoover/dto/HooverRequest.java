package com.rationaldata.robotic_hoover.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HooverRequest {

    @NotNull(message = "Room size cannot be null")
    @Size(min = 2, max = 2, message = "Room size must be an array of exactly 2 integers [x, y]")
    private int[] roomSize; // array [x, y]

    @NotNull(message = "Initial position cannot be null")
    @Size(min = 2, max = 2, message = "Initial position must be an array of exactly 2 integers [x, y]")
    private int[] coords; // array [x, y]

    @NotEmpty(message = "Patches list cannot be empty")
    private List<@Size(min = 2, max = 2, message = "Each patch must be an array of exactly 2 integers [x, y]") int[]> patches; // List of arrays [[x1, y1], [x2, y2], ...]

    @NotNull(message = "Instructions cannot be null")
    @Pattern(regexp = "^[NESW]+$", message = "Instructions must only contain the characters N, E, S, W")
    private String instructions;
}

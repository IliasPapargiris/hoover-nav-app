package com.rationaldata.robotic_hoover.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class HooverRequest {

    @NotNull(message = "Room size cannot be null")
    @Valid
    private Coords roomSize;

    @NotNull(message = "Initial coordinates cannot be null")
    @Valid
    private Coords initialPosition;

    @NotEmpty(message = "Patches list cannot be empty")
    @Valid
    private Set<Coords> patches;

    @NotNull(message = "Instructions cannot be null")
    @Pattern(regexp = "^[NESW]+$", message = "Instructions must only contain the characters N, E, S, W")
    private String instructions;
}

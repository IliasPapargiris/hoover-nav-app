package com.rationaldata.robotic_hoover.controller;

import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.service.HooverService;
import com.rationaldata.robotic_hoover.utils.JsonExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller class for handling hoover navigation requests.
 * It exposes endpoints that allow the client to control the hoover's movements and track its cleaning progress.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/hoover")
@Validated
public class HooverController {

    private final HooverService hooverService;

    @Operation(
            summary = "Navigate the hoover in the room",
            description = "Moves the hoover according to the provided instructions and cleans dirt patches in the room.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload to navigate the hoover.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HooverRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Hoover Request Example",
                                            summary = "Example of Hoover Request",
                                            value = JsonExamples.HOOVER_REQUEST_JSON
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hoover navigation completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HooverResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Hoover Response Example",
                                            summary = "Example of Hoover Response",
                                            value = JsonExamples.HOOVER_RESPONSE_JSON
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Error Response",
                                            summary = "Validation Error",
                                            value = JsonExamples.VALIDATION_ERROR_JSON
                                    )
                            }
                    )
            )
    })
    @PostMapping("/navigate")
    public ResponseEntity<HooverResponse> navigate(@Valid @RequestBody HooverRequest request) {
        HooverResponse response = hooverService.navigate(request);
        return ResponseEntity.ok(response);
    }
}

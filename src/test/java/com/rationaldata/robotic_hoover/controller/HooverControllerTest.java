package com.rationaldata.robotic_hoover.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationaldata.robotic_hoover.dto.Coords;
import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import com.rationaldata.robotic_hoover.exception.OutOfRoomBoundsCoordinatesException;
import com.rationaldata.robotic_hoover.service.HooverService;
import com.rationaldata.robotic_hoover.validation.HooverRequestValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HooverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HooverService hooverService;

    @MockBean
    private HooverRequestValidator hooverRequestValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHooverNavigationSuccessfully() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(1, 2));
        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 0));
        patches.add(new Coords(2, 2));
        patches.add(new Coords(2, 3));
        request.setPatches(patches);
        request.setInstructions("NNESEESWNWW");

        HooverResponse response = new HooverResponse(new Coords(1, 3), 1);


        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.coords.x").value(1))
                .andExpect(jsonPath("$.coords.y").value(3))
                .andExpect(jsonPath("$.patches").value(1));
    }

    @Test
    void testHooverNavigationWithInvalidInputOnDirections() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setInitialPosition(new Coords(1, 2));
        request.setRoomSize(new Coords(5,5));
        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));
        request.setPatches(patches);
        request.setInstructions("NNEA");

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{instructions=Instructions must only contain the characters N, E, S, W}"));
    }

    @Test
    void testHooverNavigationWithNegativeValuesOnCoordinates() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setInitialPosition(new Coords(1, 2));
        request.setRoomSize(new Coords(-5,-5));
        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));
        request.setPatches(patches);
        request.setInstructions("NNE");

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{roomSize.y=Y coordinate cannot be negative, roomSize.x=X coordinate cannot be negative}"));
    }

    @Test
    void testHooverNavigationWithNullField() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setInitialPosition(new Coords(1, 2));
        request.setRoomSize(null);
        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));
        request.setPatches(patches);
        request.setInstructions("NNE");

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{roomSize=Room size cannot be null}"));
    }

    @Test
    void testHooverNavigationWithEmptyPatches() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setInitialPosition(new Coords(1, 2));
        request.setRoomSize(new Coords(5,5));
        Set<Coords> patches = new HashSet<>();
        request.setPatches(patches);
        request.setInstructions("NNE");

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{patches=Patches list cannot be empty}"));
    }

    @Test
    void testHooverNavigationWithInvalidRoomSize() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(0, 0));
        request.setInitialPosition(new Coords(1, 2));
        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));
        request.setPatches(patches);
        request.setInstructions("NNE");

        doThrow(new InvalidRoomSizeException("Both room width and height cannot be zero."))
                .when(hooverRequestValidator).validateHooverRequest(any(HooverRequest.class));

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Room Size"))
                .andExpect(jsonPath("$.message").value("Both room width and height cannot be zero."));

        verify(hooverRequestValidator, times(1)).validateHooverRequest(any(HooverRequest.class));
    }

    @Test
    void testHooverNavigationWithOutOfBoundsPatchesCoordinates() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));  // Valid room size
        request.setInitialPosition(new Coords(1, 2));  // Valid initial position

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));  // Valid patch
        patches.add(new Coords(6, 6));  // Out of bounds patch
        request.setPatches(patches);
        request.setInstructions("NNE");

        doThrow(new OutOfRoomBoundsCoordinatesException("Patch coordinates are out of bounds of the room size."))
                .when(hooverRequestValidator).validateHooverRequest(any(HooverRequest.class));

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Out of Room Bounds"))
                .andExpect(jsonPath("$.message").value("Patch coordinates are out of bounds of the room size."));

        verify(hooverRequestValidator, times(1)).validateHooverRequest(any(HooverRequest.class));
    }

}

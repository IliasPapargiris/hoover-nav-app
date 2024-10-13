package com.rationaldata.robotic_hoover.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import com.rationaldata.robotic_hoover.exception.NegativeValuesException;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

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
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{1, 2});
        List<int[]> patches = Arrays.asList(new int[]{1, 0}, new int[]{2, 2}, new int[]{2, 3});
        request.setPatches(patches);
        request.setInstructions("NNESEESWNWW");

        HooverResponse response = new HooverResponse(new int[]{1, 3}, 1);

        doNothing().when(hooverRequestValidator).validateHooverRequest(any(HooverRequest.class));

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.coords[0]").value(1))
                .andExpect(jsonPath("$.coords[1]").value(3))
                .andExpect(jsonPath("$.patches").value(1));

        verify(hooverRequestValidator, times(1)).validateHooverRequest(any(HooverRequest.class));
    }
    @Test
    void testHooverNavigationWithInvalidInputOnDirections() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{1, 2});
        List<int[]> patches = Arrays.asList(new int[]{1, 1});
        request.setPatches(patches);
        request.setInstructions("NNEA"); // Invalid instruction 'A'

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("{instructions=Instructions must only contain the characters N, E, S, W}"));
    }

//    @Test
//    void testHooverNavigationWithInvalidInputOnDirections2() throws Exception {
//        // Given
//        HooverRequest request = new HooverRequest();
//        request.setRoomSize(new int[]{5, 5});
//        request.setCoords(new int[]{1, 2});
//        List<int[]> patches = Arrays.asList(new int[]{1, 1});
//        request.setPatches(patches);
//        request.setInstructions("NNEA"); // Invalid instruction 'A'
//
//        // Print request JSON for debugging
//        ObjectMapper objectMapper = new ObjectMapper();
//        String requestJson = objectMapper.writeValueAsString(request);
//        System.out.println("Request JSON: " + requestJson);
//
//        // Perform the request and capture the result
//        MvcResult result = mockMvc.perform(post("/hoover/navigate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestJson))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Validation Failed"))
//                .andExpect(jsonPath("$.message").value("{instructions=Instructions must only contain the characters N, E, S, W}"))
//                .andReturn();
//
//        // Print response JSON for debugging
//        String responseJson = result.getResponse().getContentAsString();
//        System.out.println("Response JSON: " + responseJson);
//    }

    @Test
    void testHooverNavigationWithNegativeValuesOnCoordinates() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{-5, -5}); // Invalid room size (negative values)
        request.setCoords(new int[]{1, 2});
        List<int[]> patches = Arrays.asList(new int[]{1, 1});
        request.setPatches(patches);
        request.setInstructions("NNE");

        // Mock the validator to throw the NegativeValuesException
        doThrow(new NegativeValuesException("Coordinates values regarding room size, patches, and initial position cannot be negative."))
                .when(hooverRequestValidator).validateHooverRequest(any(HooverRequest.class));

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Negative Values Error"))
                .andExpect(jsonPath("$.message").value("Coordinates values regarding room size, patches, and initial position cannot be negative."));
    }

    @Test
    void testHooverNavigationWithNullField() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(null); // Invalid room size (null)
        request.setCoords(new int[]{1, 2});
        List<int[]> patches = Arrays.asList(new int[]{1, 1});
        request.setPatches(patches);
        request.setInstructions("NNE");

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("{roomSize=Room size cannot be null}"));
    }

    @Test
    void testHooverNavigationWithEmptyPatches() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{1, 2});
        request.setPatches(Arrays.asList()); // Empty patches list
        request.setInstructions("NNE");

        // When & Then
        mockMvc.perform(post("/hoover/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("{patches=Patches list cannot be empty}"));
    }

    @Test
    void testHooverNavigationWithInvalidRoomSize() throws Exception {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{0, 0});  // Invalid room size
        request.setCoords(new int[]{1, 2});
        List<int[]> patches = Arrays.asList(new int[]{1, 1});
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
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{1, 2});
        List<int[]> patches = Arrays.asList(new int[]{1, 1}, new int[]{6, 6}); // 6,6 out of bounds
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

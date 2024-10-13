package com.rationaldata.robotic_hoover.validation;

import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = HooverRequestValidator.class)
class HooverRequestValidatorTest {

    private final HooverRequestValidator validator = new HooverRequestValidator();

    @Test
    void testInvalidRoomSize() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{0, 0});  // Invalid room size
        request.setCoords(new int[]{1, 1});

        List<int[]> patches = List.of(new int[]{1, 0});
        request.setPatches(patches);

        // When & Then
        Exception exception = assertThrows(InvalidRoomSizeException.class, () -> validator.validateHooverRequest(request));
        assertEquals("Both room width and height must be greater than zero.", exception.getMessage());
    }

    @Test
    void testInvalidInitialPositionOutOfBounds() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{6, 6});  // Initial position out of bounds

        List<int[]> patches = List.of(new int[]{1, 1});
        request.setPatches(patches);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateHooverRequest(request), "Initial coordinates are out of bounds of the room size.");
    }

    @Test
    void testInvalidPatchPositionOutOfBounds() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{2, 2});

        List<int[]> patches = List.of(new int[]{6, 6});  // Patch out of bounds
        request.setPatches(patches);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateHooverRequest(request), "Patch coordinates are out of bounds of the room size.");
    }

    @Test
    void testValidRoomSizeButOutOfBoundsInitialCoords() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{10, 10});  // Initial coordinates out of bounds

        List<int[]> patches = List.of(new int[]{1, 1});
        request.setPatches(patches);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateHooverRequest(request), "Initial coordinates are out of bounds of the room size.");
    }
}

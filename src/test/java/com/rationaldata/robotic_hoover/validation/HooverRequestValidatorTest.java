package com.rationaldata.robotic_hoover.validation;

import com.rationaldata.robotic_hoover.dto.Coords;
import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = HooverRequestValidator.class)
class HooverRequestValidatorTest {

    private final HooverRequestValidator validator = new HooverRequestValidator();

    @Test
    void testInvalidRoomSize() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(0, 0));  // Invalid room size
        request.setInitialPosition(new Coords(1, 1));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 0));
        request.setPatches(patches);

        // When & Then
        Exception exception = assertThrows(InvalidRoomSizeException.class, () -> validator.validateHooverRequest(request));
        assertEquals("Both room width and height cannot be zero.", exception.getMessage());
    }

    @Test
    void testInvalidInitialPositionOutOfBounds() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(6, 6));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));
        request.setPatches(patches);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateHooverRequest(request), "Initial coordinates are out of bounds of the room size.");
    }

    @Test
    void testInvalidPatchPositionOutOfBounds() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(2, 2));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(6, 6));
        request.setPatches(patches);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateHooverRequest(request), "Patch coordinates are out of bounds of the room size.");
    }

    @Test
    void testValidRoomSizeButOutOfBoundsInitialCoords() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(10, 10));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 1));
        request.setPatches(patches);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateHooverRequest(request), "Initial coordinates are out of bounds of the room size.");
    }
}
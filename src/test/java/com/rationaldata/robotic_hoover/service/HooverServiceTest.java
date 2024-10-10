package com.rationaldata.robotic_hoover.service;

import com.rationaldata.robotic_hoover.dto.Coords;
import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import com.rationaldata.robotic_hoover.validation.HooverRequestValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {HooverService.class, HooverRequestValidator.class})
class HooverServiceTest {

    @Autowired
    private HooverService hooverService;

    @Test
    void testHooverNavigationSuccessfully() {
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

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertEquals(new Coords(1, 3), response.getCoords(), "The final coordinates should be (1, 3)");
        assertEquals(1, response.getPatches(), "The number of cleaned patches should be 1");
    }

    @Test
    void testHooverNavigationWithNoPatchesCleaned() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(0, 0));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(4, 4));  // No reachable patches
        request.setPatches(patches);
        request.setInstructions("NNNN");

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertEquals(new Coords(0, 4), response.getCoords(), "The final coordinates should be (0, 4)");
        assertEquals(0, response.getPatches(), "The number of cleaned patches should be 0");
    }

    @Test
    void testHooverSkiddingAtWall() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(4, 4));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(4, 4));  // Hoover starts at a patch
        request.setPatches(patches);
        request.setInstructions("EEEE");  // Instruction trying to go outside room

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertEquals(new Coords(5, 4), response.getCoords(), "The final coordinates should be (5, 4)");
        assertEquals(1, response.getPatches(), "The number of cleaned patches should be 1");
    }

    @Test
    void testHooverRevisitsCleanedPatch() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(1, 1));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 0));
        patches.add(new Coords(2, 2));
        request.setPatches(patches);
        request.setInstructions("SSEEWS");

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertEquals(new Coords(2, 0), response.getCoords(), "The final coordinates should be (2, 0)");
        assertEquals(1, response.getPatches(), "The number of cleaned patches should be 1");
    }

    @Test
    void testHooverNavigationWithInvalidRoomSize() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(0, 0)); // Invalid room size
        request.setInitialPosition(new Coords(1, 1));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 0));
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        assertThrows(InvalidRoomSizeException.class, () -> hooverService.navigate(request), "Both room width and height cannot be zero.");
    }

    @Test
    void testInvalidInitialPosition() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(6, 6)); // Invalid initial position (out of bounds)

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 0));
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hooverService.navigate(request), "Initial coordinates are out of bounds of the room size.");
    }

    @Test
    void testInvalidPatchPosition() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(5, 5));
        request.setInitialPosition(new Coords(2, 2));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(6, 6)); // Invalid patch position (out of bounds)
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hooverService.navigate(request), "Patch coordinates are out of bounds of the room size.");
    }

    @Test
    void testInvalidRoomSizeZeroByZero() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new Coords(0, 0));  // Invalid room size (0,0)
        request.setInitialPosition(new Coords(1, 1));

        Set<Coords> patches = new HashSet<>();
        patches.add(new Coords(1, 0));  // Some patches
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        Exception exception = assertThrows(InvalidRoomSizeException.class, () -> hooverService.navigate(request));
        assertEquals("Both room width and height cannot be zero.", exception.getMessage());
    }
}

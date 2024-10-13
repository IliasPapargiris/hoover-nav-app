package com.rationaldata.robotic_hoover.service;

import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import com.rationaldata.robotic_hoover.validation.HooverRequestValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {HooverService.class, HooverRequestValidator.class})
class HooverServiceTest {

    @Autowired
    private HooverService hooverService;

    @Test
    void testHooverNavigationSuccessfully() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{1, 2});

        List<int[]> patches = new ArrayList<>(Arrays.asList(
                new int[]{1, 0},
                new int[]{2, 2},
                new int[]{2, 3}
        ));
        request.setPatches(patches);
        request.setInstructions("NNESEESWNWW");

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertArrayEquals(new int[]{1, 3}, response.getCoords(), "The final coordinates should be (1, 3)");
        assertEquals(1, response.getPatches(), "The number of cleaned patches should be 1");
    }


    @Test
    void testHooverNavigationWithNoPatchesCleaned() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{0, 0});

        List<int[]> patches = new ArrayList<>(Arrays.asList(new int[]{4, 4}));

        request.setPatches(patches);
        request.setInstructions("NNNN");

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertArrayEquals(new int[]{0, 4}, response.getCoords(), "The final coordinates should be (0, 4)");
        assertEquals(0, response.getPatches(), "The number of cleaned patches should be 0");
    }

    @Test
    void testHooverSkiddingAtWall() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{4, 4});
        List<int[]> patches = new ArrayList<>(Arrays.asList(new int[]{4, 4}));
        request.setPatches(patches);
        request.setInstructions("EEEE");  // Instruction trying to go outside room

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertArrayEquals(new int[]{5, 4}, response.getCoords(), "The final coordinates should be (5, 4)");
        assertEquals(1, response.getPatches(), "The number of cleaned patches should be 1");
    }

    @Test
    void testHooverRevisitsCleanedPatch() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{1, 1});

        List<int[]> patches = new ArrayList<>(Arrays.asList(
                new int[]{1, 0},
                new int[]{2, 2}
        ));
        request.setPatches(patches);
        request.setInstructions("SSEEWS");

        // When
        HooverResponse response = hooverService.navigate(request);

        // Then
        assertNotNull(response);
        assertArrayEquals(new int[]{2, 0}, response.getCoords(), "The final coordinates should be (2, 0)");
        assertEquals(1, response.getPatches(), "The number of cleaned patches should be 1");
    }


    @Test
    void testHooverNavigationWithInvalidRoomSize() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{0, 0}); // Invalid room size
        request.setCoords(new int[]{1, 1});

        List<int[]> patches = List.of(
                new int[]{1, 0}
        );
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        assertThrows(InvalidRoomSizeException.class, () -> hooverService.navigate(request), "Both room width and height cannot be zero.");
    }

    @Test
    void testInvalidInitialPosition() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{6, 6}); // Invalid initial position (out of bounds)

        List<int[]> patches = List.of(
                new int[]{1, 0}
        );
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hooverService.navigate(request), "Initial coordinates are out of bounds of the room size.");
    }

    @Test
    void testInvalidPatchPosition() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{5, 5});
        request.setCoords(new int[]{2, 2});

        List<int[]> patches = List.of(
                new int[]{6, 6} // Invalid patch position (out of bounds)
        );
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hooverService.navigate(request), "Patch coordinates are out of bounds of the room size.");
    }

    @Test
    void testInvalidRoomSizeZeroByZero() {
        // Given
        HooverRequest request = new HooverRequest();
        request.setRoomSize(new int[]{0, 0});  // Invalid room size (0,0)
        request.setCoords(new int[]{1, 1});

        List<int[]> patches = List.of(
                new int[]{1, 0}
        );
        request.setPatches(patches);
        request.setInstructions("N");

        // When & Then
        Exception exception = assertThrows(InvalidRoomSizeException.class, () -> hooverService.navigate(request));
        assertEquals("Both room width and height must be greater than zero.", exception.getMessage());
    }
}

package com.rationaldata.robotic_hoover.validation;

import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import com.rationaldata.robotic_hoover.exception.NegativeValuesException;
import com.rationaldata.robotic_hoover.exception.OutOfRoomBoundsCoordinatesException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class is responsible for validating the {@link HooverRequest} before processing.
 * It ensures that the room size, initial position, and dirt patches are valid and within bounds.
 */
@Component
public class HooverRequestValidator {

    public void validateHooverRequest(HooverRequest request) {
        int roomWidth = request.getRoomSize()[0];
        int roomHeight = request.getRoomSize()[1];

        if(!hasValidNonNegativeCoordinates(request)){
            throw new NegativeValuesException("Coordinates values regarding room size, patches and initial position can not be negative.");
        }

        if (!hasValidRoomSize(roomWidth, roomHeight)) {
            throw new InvalidRoomSizeException("Both room width and height must be greater than zero.");
        }

        if (!areCoordinatesWithinRoomBounds(request, roomWidth, roomHeight)) {
            throw new OutOfRoomBoundsCoordinatesException("Initial coordinates or patch coordinates are out of bounds of the room size.");
        }

        if(!patchesHavingValidSize(request.getPatches())){
            throw new IllegalArgumentException("Invalid patch ,only exactly 2 integers must be contained in a patch array");
        }
    }

    private boolean hasValidRoomSize(int roomWidth, int roomHeight) {
        return roomWidth > 0 && roomHeight > 0;
    }


    /**
     * Checks if the request contains any negative coordinates in roomSize, initialPosition, or patches.
     *
     * @param request The HooverRequest containing room size, initial position, and patches.
     * @return true if all values are non-negative, false if any negative values are found.
     */
    private boolean hasValidNonNegativeCoordinates(HooverRequest request) {
        int[] roomSize = request.getRoomSize();
        int[] initialPosition = request.getCoords();

        // Check for negative values in room size
        if (roomSize[0] < 0 || roomSize[1] < 0) {
            return false;
        }

        // Check for negative values in initial position
        if (initialPosition[0] < 0 || initialPosition[1] < 0) {
            return false;
        }

        // Check for negative values in patches
        for (int[] patch : request.getPatches()) {
            if (patch[0] < 0 || patch[1] < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the coordinates (initial position and patches) to ensure they are
     * within the room size bounds.
     * @param request The HooverRequest containing the coordinates.
     * @param roomWidth The width of the room.
     * @param roomHeight The height of the room.
     * @return true if all coordinates are non-negative and within the room bounds, false otherwise.
     */
    private boolean areCoordinatesWithinRoomBounds(HooverRequest request, int roomWidth, int roomHeight) {

        int[] initialPosition = request.getCoords();

        // Check if initial position is within room bounds
        if (initialPosition[0] > roomWidth || initialPosition[1] > roomHeight) {
            return false;
        }

        // Check if each patch is within room bounds
        for (int[] patch : request.getPatches()) {
            if (patch[0] > roomWidth || patch[1] > roomHeight) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates that each patch in the list has exactly 2 elements representing x and y coordinates.
     *
     * @param patches The list of patches to validate.
     * @return true if all patches are valid (i.e., each has exactly 2 elements), false otherwise.
     */
    private boolean patchesHavingValidSize(List<int[]> patches) {
        for (int[] patch : patches) {
            if (patch.length != 2) {
                return false; // Patch does not have exactly 2 elements (x, y)
            }
        }
        return true;
    }

}

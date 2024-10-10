package com.rationaldata.robotic_hoover.validation;

import com.rationaldata.robotic_hoover.dto.Coords;
import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException;
import com.rationaldata.robotic_hoover.exception.OutOfRoomBoundsCoordinatesException;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for validating the {@link HooverRequest} before processing.
 * It ensures that the room size, initial position, and dirt patches are valid and within bounds.
 */
@Component
public class HooverRequestValidator {

    public void validateHooverRequest(HooverRequest request) {
        int roomWidth = request.getRoomSize().getX();
        int roomHeight = request.getRoomSize().getY();

        if (!hasValidRoomSize(roomWidth, roomHeight)) {
            throw new InvalidRoomSizeException("Both room width and height cannot be zero.");
        }

        if (!areValidCoordinates(request, roomWidth, roomHeight)) {
            throw new OutOfRoomBoundsCoordinatesException("Initial coordinates or patch coordinates are out of bounds of the room size.");
        }
    }


    private boolean hasValidRoomSize(int roomWidth, int roomHeight) {
        return roomWidth > 0 && roomHeight > 0;
    }

    /**
     * Validates the coordinates (initial position and patches) based on the room size.
     *
     * @param request The HooverRequest containing the coordinates.
     * @param roomWidth  The width of the room.
     * @param roomHeight The height of the room.
     * @return true if all coordinates are within the room bounds, false otherwise.
     */
    private boolean areValidCoordinates(HooverRequest request, int roomWidth, int roomHeight) {
        Coords initialPosition = request.getInitialPosition();


        if (initialPosition.getX() > roomWidth || initialPosition.getY() > roomHeight) {
            return false;
        }

        for (Coords patch : request.getPatches()) {
            if (patch.getX() > roomWidth || patch.getY() > roomHeight) {
                return false;
            }
        }

        return true;
    }
}

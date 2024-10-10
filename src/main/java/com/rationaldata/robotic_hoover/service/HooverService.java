package com.rationaldata.robotic_hoover.service;

import com.rationaldata.robotic_hoover.dto.Coords;
import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.validation.HooverRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service responsible for handling the hoover navigation within the room.
 * It processes the movement of the hoover based on the provided instructions and cleans the dirt patches.
 */

@Service
@RequiredArgsConstructor
public class HooverService {

    private final HooverRequestValidator validator;

    /**
     * Navigates the hoover through the room based on the provided instructions and
     * cleans any dirt patches it encounters. The hoover moves one tile at a time
     * according to the instructions, which are a series of characters representing
     * cardinal directions ('N', 'E', 'S', 'W'). The hoover will clean a dirt patch
     * if it passes over it, and dirt patches can only be cleaned once.
     *
     * @param request The {@link HooverRequest} containing the room size, initial
     *                position of the hoover, list of dirt patches, and movement
     *                instructions.
     * @return A {@link HooverResponse} that contains the final position of the hoover
     *         and the number of dirt patches cleaned during the navigation.
     *
     * @throws com.rationaldata.robotic_hoover.exception.InvalidRoomSizeException if the room size is invalid.
     * @throws com.rationaldata.robotic_hoover.exception.OutOfRoomBoundsCoordinatesException if the initial position
     *         or any dirt patch coordinates are out of bounds.
     */
    public HooverResponse navigate(HooverRequest request) {
        validator.validateHooverRequest(request);

        Coords roomSize = request.getRoomSize();
        int roomWidth = roomSize.getX();
        int roomHeight = roomSize.getY();

        Coords hooverPosition = request.getInitialPosition();
        String instructions = request.getInstructions();

        Set<Coords> patches = request.getPatches();
        int cleanedPatches = 0;

        if (patches.contains(new Coords(hooverPosition.getX(), hooverPosition.getY()))) {
            cleanedPatches++;
            patches.remove(new Coords(hooverPosition.getX(), hooverPosition.getY()));
        }

        for (char instruction : instructions.toCharArray()) {
            moveHoover(hooverPosition, instruction, roomWidth, roomHeight);

            if (patches.contains(new Coords(hooverPosition.getX(), hooverPosition.getY()))) {
                cleanedPatches++;
                patches.remove(new Coords(hooverPosition.getX(), hooverPosition.getY()));
            }
        }

        return new HooverResponse(new Coords(hooverPosition.getX(), hooverPosition.getY()), cleanedPatches);
    }

    /**
     * Moves the hoover in the specified direction within the room boundaries.
     * The movement is determined by the direction character:
     * <ul>
     *   <li>'N' (North): Increases the Y coordinate by 1 (moves up).</li>
     *   <li>'S' (South): Decreases the Y coordinate by 1 (moves down).</li>
     *   <li>'E' (East): Increases the X coordinate by 1 (moves right).</li>
     *   <li>'W' (West): Decreases the X coordinate by 1 (moves left).</li>
     * </ul>
     *
     * @param position    The current {@link Coords} position of the hoover.
     * @param direction   The direction ('N', 'S', 'E', 'W') in which to move the hoover.
     * @param roomWidth   The width of the room.
     * @param roomHeight  The height of the room.
     */
    private void moveHoover(Coords position, char direction, int roomWidth, int roomHeight) {
        if (headingToWall(position, direction, roomWidth, roomHeight)) {
            return;
        }

        switch (direction) {
            case 'N':
                position.setY(position.getY() + 1);
                break;
            case 'S':
                position.setY(position.getY() - 1);
                break;
            case 'E':
                position.setX(position.getX() + 1);
                break;
            case 'W':
                position.setX(position.getX() - 1);
                break;
        }
    }

    /**
     * Checks if the hoover is attempting to move outside the room boundaries.
     *
     * @param position    The current {@link Coords} position of the hoover.
     * @param direction   The direction in which the hoover intends to move ('N', 'S', 'E', 'W').
     * @param roomWidth   The width of the room.
     * @param roomHeight  The height of the room.
     * @return {@code true} if the hoover is trying to move outside the room boundaries, {@code false} otherwise.
     */
    private boolean headingToWall(Coords position, char direction, int roomWidth, int roomHeight) {
        switch (direction) {
            case 'N':
                return position.getY() >= roomHeight;
            case 'S':
                return position.getY() <= 0;
            case 'E':
                return position.getX() >= roomWidth;
            case 'W':
                return position.getX() <= 0;
            default:
                return false;
        }
    }

}
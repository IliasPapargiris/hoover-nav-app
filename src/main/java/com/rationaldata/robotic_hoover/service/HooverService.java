package com.rationaldata.robotic_hoover.service;

import com.rationaldata.robotic_hoover.dto.HooverRequest;
import com.rationaldata.robotic_hoover.dto.HooverResponse;
import com.rationaldata.robotic_hoover.validation.HooverRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

        int[] roomSize = request.getRoomSize();
        int roomWidth = roomSize[0];
        int roomHeight = roomSize[1];

        int[] hooverPosition = request.getCoords();
        String instructions = request.getInstructions();

        List<int[]> patches = request.getPatches();
        int cleanedPatches = 0;

        if (containsPatch(patches, hooverPosition)) {
            cleanedPatches++;
            removePatch(patches, hooverPosition);
        }

        for (char instruction : instructions.toCharArray()) {
            moveHoover(hooverPosition, instruction, roomWidth, roomHeight);

            if (containsPatch(patches, hooverPosition)) {
                cleanedPatches++;
                removePatch(patches, hooverPosition);
            }
        }

        return new HooverResponse(new int[]{hooverPosition[0], hooverPosition[1]}, cleanedPatches);
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
     * @param position    The current hoover position (array of [x, y]).
     * @param direction   The direction ('N', 'S', 'E', 'W') in which to move the hoover.
     * @param roomWidth   The width of the room.
     * @param roomHeight  The height of the room.
     */
    private void moveHoover(int[] position, char direction, int roomWidth, int roomHeight) {
        if (headingToWall(position, direction, roomWidth, roomHeight)) {
            return;
        }

        switch (direction) {
            case 'N':
                position[1]++;
                break;
            case 'S':
                position[1]--;
                break;
            case 'E':
                position[0]++;
                break;
            case 'W':
                position[0]--;
                break;
        }
    }

    /**
     * Checks if the hoover is attempting to move outside the room boundaries.
     *
     * @param position    The current hoover position (array of [x, y]).
     * @param direction   The direction in which the hoover intends to move ('N', 'S', 'E', 'W').
     * @param roomWidth   The width of the room.
     * @param roomHeight  The height of the room.
     * @return {@code true} if the hoover is trying to move outside the room boundaries, {@code false} otherwise.
     */
    private boolean headingToWall(int[] position, char direction, int roomWidth, int roomHeight) {
        switch (direction) {
            case 'N':
                return position[1] >= roomHeight;
            case 'S':
                return position[1] <= 0;
            case 'E':
                return position[0] >= roomWidth;
            case 'W':
                return position[0] <= 0;
            default:
                return false;
        }
    }

    /**
     * Checks if the hoover is on a dirt patch.
     *
     * @param patches   The list of patches.
     * @param position  The current hoover position.
     * @return {@code true} if the hoover is on a patch, {@code false} otherwise.
     */
    private boolean containsPatch(List<int[]> patches, int[] position) {
        return patches.stream().anyMatch(patch -> patch[0] == position[0] && patch[1] == position[1]);
    }

    /**
     * Removes a patch from the list after it has been cleaned.
     *
     * @param patches   The list of patches.
     * @param position  The current hoover position.
     */
    private void removePatch(List<int[]> patches, int[] position) {
        patches.removeIf(patch -> patch[0] == position[0] && patch[1] == position[1]);
    }
}

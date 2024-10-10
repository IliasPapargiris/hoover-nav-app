package com.rationaldata.robotic_hoover.exception;

public class OutOfRoomBoundsCoordinatesException extends IllegalArgumentException{
    public OutOfRoomBoundsCoordinatesException(String message) {
        super(message);
    }
}

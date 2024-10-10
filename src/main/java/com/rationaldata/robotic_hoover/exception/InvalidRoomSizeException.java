package com.rationaldata.robotic_hoover.exception;

public class InvalidRoomSizeException extends IllegalArgumentException {
    public InvalidRoomSizeException(String message) {
        super(message);
    }
}

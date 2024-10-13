package com.rationaldata.robotic_hoover.utils;

public class JsonExamples {

    // Example of Hoover Request JSON
    public static final String HOOVER_REQUEST_JSON = """
            {
              "roomSize": [5, 5],
              "coords": [1, 2],
              "patches": [[1, 0], [2, 2]],
              "instructions": "NNESEESWNWW"
            }""";

    // Example of Hoover Response JSON
    public static final String HOOVER_RESPONSE_JSON = """
            {
              "coords": [1, 3],
              "patches": 2
            }""";

    // Example of Validation Error JSON
    public static final String VALIDATION_ERROR_JSON = """
            {
              "error": "Validation Failed",
              "message": "Instructions must only contain the characters N, E, S, W",
              "status": 400,
              "timestamp": "2024-10-10T11:59:00.487815"
            }""";
}

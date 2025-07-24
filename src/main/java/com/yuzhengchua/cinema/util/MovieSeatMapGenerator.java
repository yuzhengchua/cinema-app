package com.yuzhengchua.cinema.util;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

public class MovieSeatMapGenerator {

    /**
     * Logger instance for logging invalid seat format errors.
     */
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(InputValidator.class);

    /**
     * Private constructor to prevent instantiation, as this is a utility class.
     * 
     * @throws UnsupportedOperationException If this constructor is called.
     */
    private MovieSeatMapGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String[] generateMovieSeatMap(String[] input) {
        logger.debug("Generating seat map:");
        if (input == null || input.length < 3) {
            throw new IllegalArgumentException("Input string cannot be null or less than length 3");
        }
        String movieTitle = "";
        String row = input[input.length-2];
        String col = input[input.length-1];
        for (int i = 0; i < input.length-2; i++) {
            movieTitle += input[i] + " ";
        }
        return new String[] {movieTitle.trim(), row, col};


        
    }
}
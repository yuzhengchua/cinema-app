package com.yuzhengchua.cinema.util;

import org.apache.logging.log4j.Logger;

import com.yuzhengchua.cinema.constants.Constants;

/**
 * The {@code InputValidator} class provides utility methods for validating various user inputs 
 * related to cinema seat booking, including seat map formats, seat availability, and seat selection formats.
 * This class is not intended to be instantiated, as it contains only static methods.
 * <p>
 * It includes methods for validating seat map input format, checking if a string is numeric, 
 * ensuring valid row and seat configurations, and verifying seat selection format.
 * </p>
 * 
 * @since 1.0
 */
public class InputValidator {

    /**
     * Logger instance for logging invalid seat format errors.
     */
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(InputValidator.class);

    /**
     * Private constructor to prevent instantiation, as this is a utility class.
     * 
     * @throws UnsupportedOperationException If this constructor is called.
     */
    private InputValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Validates the format of the seat map input. The input must consist of three elements:
     * - A movie title that contains alphanumeric characters.
     * - The number of rows (a numeric value).
     * - The number of seats per row (a numeric value).
     * 
     * @param input The input array consisting of the movie title, number of rows, and seats per row.
     * @return {@code true} if the input format is valid; {@code false} otherwise.
     */
    public static boolean isValidSeatMapFormat(String[] input) {
        logger.debug("Validating seat map format:");
        return input != null && input.length == 3 && input[0].matches("[a-zA-Z0-9]+") && isNumeric(input[1]) && isNumeric(input[2]);
    }

    /**
     * Checks whether a string is a valid numeric value.
     * 
     * @param str The string to be checked.
     * @return {@code true} if the string is a valid numeric value; {@code false} otherwise.
     */
    public static boolean isNumeric(String str) {
        logger.debug("Checking if value is numeric");
        if (str != null) {
            try {
                Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Validates whether the number of rows and seats per row are within valid bounds.
     * The number of rows and seats per row must be positive and fall within a predefined range 
     * (specified by {@link Constants}).
     * 
     * @param rows The number of rows in the seat map.
     * @param seatsPerRow The number of seats per row in the seat map.
     * @return {@code true} if the row and seat values are valid; {@code false} otherwise.
     */
    public static boolean isValidRowAndSeats(int rows, int seatsPerRow) {
        logger.debug("Validating rows and seats: rows={}, seatsPerRow={}", rows, seatsPerRow);
        return rows >= Constants.MIN_ROWS && rows <= Constants.MAX_ROWS 
                && seatsPerRow >= Constants.MIN_SEATS_PER_ROW
                && seatsPerRow <= Constants.MAX_SEATS_PER_ROW;
    }

    /**
     * Validates the format of a chosen seat. The seat format must match a specific pattern, 
     * which allows seats to be represented as:
     * - A letter followed by a number (e.g., A1, B12).
     * - Specific patterns (e.g., A01, A50).
     * 
     * @param seat The seat string to be validated.
     * @return {@code true} if the seat format is valid; {@code false} otherwise.
     */
    public static boolean isValidChosenSeat(String seat) {
        logger.debug("Validating chosen seat format: {}", seat);
        if (seat != null && seat.matches("^[A-Z]0[1-9]$|^[A-Z][1-9]$|^[A-Z][1-4][\\d]$|^[A-Z]50$")) {
            return true;
        } else {
            logger.error("Invalid seat format: {}", seat);
            return false;
        }
    }
}

package com.yuzhengchua.cinema.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InputValidatorTest {
   
    @Test
    void testValidSeatMapFormat() {
        assert(InputValidator.isValidSeatMapFormat("Movie 5 10".split(" ")));
        assertFalse(InputValidator.isValidSeatMapFormat(null));
        assertFalse(InputValidator.isValidSeatMapFormat("Movie 5".split(" ")));
        assertFalse(InputValidator.isValidSeatMapFormat("Movie-a 5 10".split(" ")));
        assertFalse(InputValidator.isValidSeatMapFormat("Movie asd 10".split(" ")));
        assertFalse(InputValidator.isValidSeatMapFormat("Movie 10 asd".split(" ")));
        assertTrue(InputValidator.isValidSeatMapFormat("Movie with a space 10 10".split(" ")));
        assertTrue(InputValidator.isValidSeatMapFormat("Movie 5 10 20".split(" ")));
    }   

    @Test
    void testIsNumeric() {
        assert(InputValidator.isNumeric("123"));
        assertFalse(InputValidator.isNumeric("abc"));
        assertFalse(InputValidator.isNumeric(null));
    }

    @Test
    void testIsValidRowAndSeats() {
        assert(InputValidator.isValidRowAndSeats(5, 10));
        assertFalse(InputValidator.isValidRowAndSeats(0, 10));
        assertFalse(InputValidator.isValidRowAndSeats(5, 0));
        assertFalse(InputValidator.isValidRowAndSeats(6, 55));
        assertFalse(InputValidator.isValidRowAndSeats(27, 11));
    }

    @Test
    void testIsValidChosenSeat() {
        assert(InputValidator.isValidChosenSeat("A01"));
        assert(InputValidator.isValidChosenSeat("B10"));
        assert(InputValidator.isValidChosenSeat("C50"));
        assertFalse(InputValidator.isValidChosenSeat("A0"));
        assertFalse(InputValidator.isValidChosenSeat("A100"));
        assertFalse(InputValidator.isValidChosenSeat("Z51"));
        assertFalse(InputValidator.isValidChosenSeat("B-1"));
        assertFalse(InputValidator.isValidChosenSeat(null));
    }
}

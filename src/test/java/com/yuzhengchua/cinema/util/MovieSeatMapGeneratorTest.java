package com.yuzhengchua.cinema.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieSeatMapGeneratorTest {
    @Test
    void testGenerateMovieSeatMap() {
        String[] expected = new String[] { "Movie with a space", "10", "10" };
        String[] result = MovieSeatMapGenerator.generateMovieSeatMap("Movie with a space 10 10".split(" "));
        assertEquals(expected[0], result[0]);
        assertEquals(expected[1], result[1]);
        assertEquals(expected[2], result[2]);
    }

    @Test
    void testVoidInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MovieSeatMapGenerator.generateMovieSeatMap(null);
        });
        String expectedMessage = "Input string cannot be null or less than length 3";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }
    @Test
    void testInputLessThanLengthThree() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MovieSeatMapGenerator.generateMovieSeatMap("Movie 10".split(" "));
        });
        String expectedMessage = "Input string cannot be null or less than length 3";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }
}

package com.yuzhengchua.cinema.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;

class CinemaAppRunnerTest {
    @Mock
    Scanner mockScanner;
    CinemaAppRunner runner;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        runner = new CinemaAppRunner(mockScanner);
    }

    @Test
    void testInitialiseCinemaWithInvalidInputFormat() {
        Mockito.when(mockScanner.nextLine()).thenReturn("InvalidInput");
        runner.initialiseCinema();
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please provide a valid format"));
    }

    @Test
    void testInitialiseCinemaWithInvalidRowOrSeats() {
        Mockito.when(mockScanner.nextLine()).thenReturn("Movie 0 100");
        runner.initialiseCinema();
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Rows should be > 0 and <= 26 while seats per row should be > 0 and <= 50"));
    }

    @Test
    void testInitialiseCinemaWithValidInput() {
        // This will go into CinemaWorkflowService.listActions() which loops forever, so we only test up to valid input
        Mockito.when(mockScanner.nextLine()).thenReturn("Movie 5 5");
        // To avoid infinite loop, we can mock CinemaWorkflowService if refactored, but for now, just check output up to that point
        assertDoesNotThrow(() -> runner.initialiseCinema());
        String output = outContent.toString();
        assertFalse(output.contains("Invalid input. Please provide a valid format"));
        assertFalse(output.contains("Invalid input. Rows should be > 0 and <= 26 while seats per row should be > 0 and <= 50"));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
} 
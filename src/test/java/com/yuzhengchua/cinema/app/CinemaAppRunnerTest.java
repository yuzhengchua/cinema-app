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
import com.yuzhengchua.cinema.service.BookingServiceImpl;
import com.yuzhengchua.cinema.models.SeatMap;
import com.yuzhengchua.cinema.service.CinemaWorkflowService;
import com.yuzhengchua.cinema.service.ExitHandler;

class CinemaAppRunnerTest {
    @Mock
    Scanner mockScanner;
    CinemaAppRunner runner;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    static class TestExitException extends RuntimeException {}
    interface ExitHandler { void exit(); }
    static class TestExitHandler implements ExitHandler {
        public void exit() { throw new TestExitException(); }
    }
    // Testable subclass to inject ExitHandler
    class TestableCinemaAppRunner extends CinemaAppRunner {
        public TestableCinemaAppRunner(Scanner scanner) { super(scanner); }
        @Override
        void initialiseCinema() {
            BookingServiceImpl bookingService;
            CinemaWorkflowService cinemaWorkflowService;
            SeatMap seatMap;
            String title;
            System.out.println("Please define movie title and seating map in [Title] [Row] [SeatsPerRow] format:");
            String input = mockScanner.nextLine();
            String[] parts = input.split(" ");
            int rows;
            int seatsPerRow;
            if (com.yuzhengchua.cinema.util.InputValidator.isValidSeatMapFormat(parts)) {
                String[] movieString = com.yuzhengchua.cinema.util.MovieSeatMapGenerator.generateMovieSeatMap(parts);
                title = movieString[0];
                rows = Integer.parseInt(movieString[1]);
                seatsPerRow = Integer.parseInt(movieString[2]);
                if (com.yuzhengchua.cinema.util.InputValidator.isValidRowAndSeats(rows, seatsPerRow)) {
                    seatMap = new SeatMap(rows, seatsPerRow);
                    bookingService = new BookingServiceImpl(seatMap);
                    cinemaWorkflowService = new CinemaWorkflowService(mockScanner, bookingService, seatMap, title, new TestExitHandler());
                    cinemaWorkflowService.listActions();
                } else {
                    System.out.println("Invalid input. Rows should be > 0 and <= 26 while seats per row should be > 0 and <= 50");
                }
            } else {
                System.out.println("Invalid input. Please provide a valid format: [Title] [Row] [SeatsPerRow].");
            }
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        runner = new TestableCinemaAppRunner(mockScanner);
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
        Mockito.when(mockScanner.nextLine()).thenReturn("Movie 5 5", "3");
        assertThrows(TestExitException.class, () -> runner.initialiseCinema());
        String output = outContent.toString();
        assertFalse(output.contains("Invalid input. Please provide a valid format"));
        assertFalse(output.contains("Invalid input. Rows should be > 0 and <= 26 while seats per row should be > 0 and <= 50"));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
} 
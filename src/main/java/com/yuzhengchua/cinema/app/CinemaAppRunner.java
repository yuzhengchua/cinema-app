package com.yuzhengchua.cinema.app;

import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.yuzhengchua.cinema.models.SeatMap;
import com.yuzhengchua.cinema.service.BookingServiceImpl;
import com.yuzhengchua.cinema.service.CinemaWorkflowService;
import com.yuzhengchua.cinema.util.InputValidator;
import com.yuzhengchua.cinema.util.MovieSeatMapGenerator;

/**
 * The CinemaAppRunner class is responsible for initializing the cinema
 * application
 * and managing the cinema workflow for booking tickets. It reads the initial
 * movie
 * title, row count, and seats per row from the user, validates the input, and
 * initializes the seat map and related services.
 * <p>
 * The class implements the {@link CommandLineRunner} interface to run the
 * application
 * on startup. It interacts with the user through the console, guiding them to
 * provide
 * the necessary input to configure the cinema settings.
 * </p>
 * 
 * @since 1.0
 */
@Component
@Profile("!test")
public class CinemaAppRunner implements CommandLineRunner {

    /**
     * Logger instance for logging information and errors within the class.
     */
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(CinemaAppRunner.class);

    /**
     * Scanner instance for reading user input from the console.
     */
    private final Scanner scanner;

    /**
     * Default constructor using System.in.
     */
    public CinemaAppRunner() {
        this(new Scanner(System.in));
    }

    /**
     * Constructor for injecting a custom Scanner (for testing).
     */
    public CinemaAppRunner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * This method is executed on startup and initiates the process of defining the
     * movie title, seating arrangement, and managing the cinema workflow.
     * <p>
     * It continuously prompts the user for input and re-initializes the cinema for
     * each new session of ticket booking.
     * </p>
     * 
     * @param args Command-line arguments (not used in this class).
     * @throws Exception if any exception occurs during the execution.
     */
    @Override
    public void run(String... args) throws Exception {
        do {
            initialiseCinema();
        } while (true);
        
    }

    /**
     * This method prompts the user to define the movie title, number of rows,
     * and number of seats per row for the cinema's seating map. It validates the
     * user input and initializes the cinema services if the input is valid.
     * <p>
     * If the input format is invalid, the user is prompted again to provide the
     * correct format.
     * </p>
     */
    void initialiseCinema() {
        logger.info("GIC Cinemas application started.");
        BookingServiceImpl bookingService;
        CinemaWorkflowService cinemaWorkflowService;
        SeatMap seatMap;
        String title;
        System.out.println("Please define movie title and seating map in [Title] [Row] [SeatsPerRow] format:");
        String input = scanner.nextLine();
        String[] parts = input.split(" ");
        int rows;
        int seatsPerRow;

        // Validate the input format
        if (InputValidator.isValidSeatMapFormat(parts)) {
            String[] movieString = MovieSeatMapGenerator.generateMovieSeatMap(parts);
            title = movieString[0];
            rows = Integer.parseInt(movieString[1]);
            seatsPerRow = Integer.parseInt(movieString[2]);

            // Check if row and seat count are valid
            if (InputValidator.isValidRowAndSeats(rows, seatsPerRow)) {
                logger.info("Valid input. Proceeding with the application.");

                // Initialize the cinema services
                seatMap = new SeatMap(rows, seatsPerRow);
                bookingService = new BookingServiceImpl(seatMap);
                cinemaWorkflowService = new CinemaWorkflowService(scanner, bookingService, seatMap, title);
                cinemaWorkflowService.listActions();
            } else {
                System.out.println("Invalid input. Rows should be > 0 and <= 26 while seats per row should be > 0 and <= 50");
            }
        } else {
            System.out.println("Invalid input. Please provide a valid format: [Title] [Row] [SeatsPerRow].");
        }
    }
}

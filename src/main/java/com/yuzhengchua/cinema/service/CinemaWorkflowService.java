package com.yuzhengchua.cinema.service;

import java.util.Scanner;

import com.yuzhengchua.cinema.enums.Actions;
import com.yuzhengchua.cinema.models.SeatMap;
import com.yuzhengchua.cinema.util.InputValidator;
import com.yuzhengchua.cinema.service.ExitHandler;

/**
 * The {@code CinemaWorkflowService} class handles the interaction with the user
 * during the cinema booking process.
 * It provides methods for booking tickets, confirming bookings, checking
 * booking status, and handling user actions
 * through a command-line interface.
 * <p>
 * This class allows users to select and reserve seats, view available bookings,
 * and manage cinema-related actions.
 * </p>
 * 
 * @since 1.0
 */
// Production implementation
class SystemExitHandler implements ExitHandler {
    public void exit() {
        System.exit(0);
    }
}

public class CinemaWorkflowService {
    /**
     * Logger
     */
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager
            .getLogger(CinemaWorkflowService.class);

    /**
     * The scanner instance used for reading user input.
     */
    private final Scanner scanner;

    /**
     * The booking service used to handle seat bookings.
     */
    private final BookingServiceImpl bookingService;

    /**
     * The seat map representing the seating arrangement in the cinema.
     */
    private final SeatMap seatMap;

    /**
     * The title of the movie being shown in the cinema.
     */
    private String title;

    /**
     * The handler for exiting the application.
     */
    private final ExitHandler exitHandler;

    // Production constructor
    public CinemaWorkflowService(Scanner scanner, BookingServiceImpl bookingService, SeatMap seatMap, String title) {
        this(scanner, bookingService, seatMap, title, new SystemExitHandler());
    }

    // Testable constructor
    public CinemaWorkflowService(Scanner scanner, BookingServiceImpl bookingService, SeatMap seatMap, String title, ExitHandler exitHandler) {
        this.scanner = scanner;
        this.bookingService = bookingService;
        this.seatMap = seatMap;
        this.title = title;
        this.exitHandler = exitHandler;
        logger.info("CinemaWorkflowService initialized with movie title: {}", title);
    }

    /**
     * Handles the booking process for the first use case: booking tickets.
     * The user is prompted to enter the number of tickets to book, and then seats
     * are selected for booking.
     * After successful seat selection, the booking is confirmed.
     */
    public void runCaseOne() {
        int seatsToBook = 0;
        String numberOfTickets = "";
        String input;

        while (numberOfTickets.equalsIgnoreCase("")) {
            System.out.println("Enter number of tickets to book, or enter blank to go back to main menu:");

            numberOfTickets = scanner.nextLine();
            if (numberOfTickets.trim().isEmpty()) {
                logger.info("User chose to return to main menu without booking.");
                break;
            }

            if (!InputValidator.isNumeric(numberOfTickets)) {
                System.out.println("Please enter a valid number of tickets.");
                logger.warn("Invalid number input: {}", numberOfTickets);
                numberOfTickets = "";
                continue;
            }

            seatsToBook = Integer.parseInt(numberOfTickets);
            logger.info("User requested to book {} seats", seatsToBook);

            if (bookingService.checkSeatsAvailability(seatsToBook)
                    && bookingService.checkSeatsMoreThanZero(seatsToBook)) {
                logger.info("Seats available and valid number of tickets");
                System.out.printf("Successfully reserved %s %s tickets.%n", seatsToBook, title);
                String bookingId = bookingService.getBookingId();
                logger.debug("Get booking ID: {}", bookingId);
                String designatedSeats = "";
                while (true) {
                    try {
                        int[][] plannedCoordinates = bookingFlow(seatsToBook, designatedSeats, bookingId);
                        input = scanner.nextLine();
                        if (input.trim().isEmpty()) {
                            confirmBooking(plannedCoordinates, bookingId);
                            designatedSeats = "";
                            break;
                        } else {
                            while (!InputValidator.isValidChosenSeat(input)) {
                                System.out.println("Invalid seat selection. Please try again.");
                                logger.warn("User entered invalid designated seat: {}", input);
                                input = scanner.nextLine();
                            }
                            designatedSeats = input;
                            logger.info("User designated seat input: {}", designatedSeats);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Unexpected error ocurred. Please try again");
                        logger.error("Unexpected error ocurred. {}", e.getMessage());
                        designatedSeats = "";
                    }

                }

            } else if (!bookingService.checkSeatsAvailability(seatsToBook)) {
                System.out.println("Sorry, there are only " + seatMap.getAvailableSeats() + " seats available.");
                logger.warn("Requested {} seats, but only {} available", seatsToBook, seatMap.getAvailableSeats());
                numberOfTickets = "";
            } else {
                System.out.println("Cannot enter 0 tickets");
                logger.warn("User attempted to book 0 or negative number of tickets");
                numberOfTickets = "";
            }
        }
    }

    /**
     * Handles the booking status check process for the second use case: viewing
     * booking status.
     * The user is prompted to enter a booking ID, and the booking details are
     * displayed.
     */
    public void runCaseTwo() {
        System.out.println("Enter booking id, or enter to go back to main menu:");
        String input = scanner.nextLine();
        while (true) {
            if (input != null && !input.isEmpty() && bookingService.getBookingIdCache().containsKey(input)) {
                logger.info("User checking booking for ID: {}", input);
                try {
                    String bookingStatus = bookingService.checkBooking(input);
                    System.out.println(bookingStatus);
                    promptCancellation();
                    String cancel = scanner.nextLine();
                    if (cancel.equalsIgnoreCase("Y")) {
                        confirmCancellation(input);
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Unexpected error ocurred. Please try again");
                    logger.error("Unexpected error ocurred." + e.getMessage());
                    input = scanner.nextLine();
                }

            } else if (input == null || input.isEmpty()) {
                logger.info("User exited booking status check.");
                break;
            } else {
                System.out.println("Invalid booking id. Please try again.");
                logger.warn("Invalid booking ID entered: {}", input);
                input = scanner.nextLine();
            }
        }
    }

    /**
     * Initiates the booking flow, prompting the user for seat selections, and
     * returns the planned seat coordinates.
     * 
     * @param seatsToBook     The number of seats to book.
     * @param designatedSeats The designated seats as a string.
     * @param bookingId       The booking ID.
     * @return A 2D array representing the planned seat coordinates.
     * @throws IllegalArgumentException when designated seats exceeds the cinema
     *                                  boundaries.
     */
    private int[][] bookingFlow(int seatsToBook, String designatedSeats, String bookingId)
            throws IllegalArgumentException {
        int[][] plannedCoordinates = bookingService.planSeats(seatsToBook, designatedSeats);
        logger.info("Planned seats for booking ID {}: {}", bookingId, (Object) plannedCoordinates);
        System.out.println("Booking id:" + bookingId);
        System.out.println("Selected seats:");
        System.out.println(bookingService.printSeatMapPlan());
        System.out.println("Enter blank to accept seat selection, or enter new seating position:");

        return plannedCoordinates;
    }

    private void promptCancellation() {
        System.out.println("Would you like to cancel this booking? Y to confirm:");
    }

    private void confirmCancellation(String bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    /**
     * Confirms the booking by updating the booking status and displaying a
     * confirmation message.
     * 
     * @param plannedCoordinates The planned seat coordinates for the booking.
     * @param bookingId          The booking ID.
     * @throws IllegalArgumentException when the booking Id is invalid.
     */
    private void confirmBooking(int[][] plannedCoordinates, String bookingId) throws IllegalArgumentException {
        bookingService.confirmBooking(plannedCoordinates);
        logger.info("Booking confirmed for ID: {}", bookingId);
        System.out.println("Booking id: " + bookingId + " confirmed.\n");
    }

    /**
     * Displays the main menu of available actions to the user.
     * Continuously prompts the user to make a valid selection.
     */
    public void listActions() {
        while (true) {
            System.out.println("Welcome to GIC Cinemas");
            System.out.println(Actions.ACTION1 + title + " " + "(" + seatMap.getAvailableSeats() + " seats available)");
            System.out.println(Actions.ACTION2);
            System.out.println(Actions.ACTION3);
            System.out.println("Please enter your selection:");
            runActions();
        }
    }

    /**
     * Handles user input for selecting actions in the main menu.
     * Depending on the user's selection, the appropriate case is executed.
     */
    private void runActions() {
        String input = scanner.nextLine();
        logger.debug("User input for menu selection: {}", input);

        if (InputValidator.isNumeric(input)) {
            switch (input) {
                case "1":
                    logger.info("User selected action: Book tickets");
                    runCaseOne();
                    break;
                case "2":
                    logger.info("User selected action: Check booking status");
                    runCaseTwo();
                    break;
                case "3":
                    logger.info("User exited the system.");
                    System.out.println("Thank you for using GIC Cinemas system. Bye!");
                    exitHandler.exit();
                    break;
                default:
                    logger.warn("User entered invalid menu option: {}", input);
                    System.out.println("Invalid selection. Please enter a number between 1 and 3.");
            }
        } else {
            logger.warn("User entered non-numeric menu option: {}", input);
            System.out.println("Invalid selection. Please enter a number between 1 and 3.");
        }
    }
}

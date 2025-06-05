package com.yuzhengchua.cinema.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.yuzhengchua.cinema.models.SeatMap;
import com.yuzhengchua.cinema.util.InputValidator;

public class BookingServiceImpl implements BookingService {
    /**
     * Logger
     */
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager
            .getLogger(BookingServiceImpl.class);
    /**
     * Fixed Booking Id prefix
     */
    private static final String BOOKING_ID_PREFIX = "GIC";
    /**
     * Seat Map model to represent a cinema seating plan.
     */
    private SeatMap seatMap;

    /**
     * A 2D array to mark seats that are reserved during planning
     */
    private int[][] seatMapPlanArr;

    /**
     * A in-memory cache of the booking ids against the seats reserved for that
     * booking id
     */
    private Map<String, int[][]> bookingIdCache;

    /**
     * Booking id counter starting from 1
     */
    private int bookingIdCounter = 1;

    /**
     * Constructor to initialize the booking service with a seat map.
     *
     * @param seatMap The seat map that represents the seating arrangement of the
     *                cinema.
     */
    public BookingServiceImpl(SeatMap seatMap) {
        this.seatMap = seatMap;
        this.seatMapPlanArr = new int[seatMap.getRows()][seatMap.getSeatsPerRow()];
        for (int i = 0; i < seatMap.getRows(); i++) {
            for (int j = 0; j < seatMap.getSeatsPerRow(); j++) {
                this.seatMapPlanArr[i][j] = seatMap.getSeatMapArr()[i][j];
            }
        }
        this.bookingIdCache = new HashMap<>();
        logger.info("Initialized BookingServiceImpl with {} rows and {} seats per row",
                seatMap.getRows(), seatMap.getSeatsPerRow());
    }

    /**
     * Confirms the booking by marking the seats as booked and updating the seat
     * map.
     *
     * @param plannedCoordinates A 2D array representing the coordinates of the
     *                           booked seats.
     * @return true if the booking is confirmed successfully.
     * @throws IllegalArgumentException if the coordinates are invalid.
     */
    public boolean confirmBooking(int[][] plannedCoordinates) throws IllegalArgumentException {
        logger.debug("Attempting to confirm booking");
        if (plannedCoordinates == null || plannedCoordinates.length == 0) {
            logger.error("Invalid booking attempt: coordinates are null or empty");
            throw new IllegalArgumentException("Invalid planned coordinates. Cannot be empty or null");
        }

        for (int[] coord : plannedCoordinates) {
            if ((coord.length == 0)
                    || (coord[0] >= this.seatMap.getRows() || coord[1] >= this.seatMap.getSeatsPerRow())) {
                logger.error("Booking coordinate out of bounds");
                throw new IllegalArgumentException("Invalid planned coordinates: " + Arrays.toString(coord));
            }
            seatMapPlanArr[coord[0]][coord[1]] = 1;
        }
        seatMap.setSeatMapArr(seatMapPlanArr);
        seatMap.setAvailableSeats(seatMap.getAvailableSeats() - plannedCoordinates.length);
        insertBookingIdSeats(getBookingId(), plannedCoordinates);
        logger.info("Booking confirmed with ID: {}", getBookingId());
        incrementBookingIdCounter();

        return true;
    }

    /**
     * Plans the seats to be booked based on the number of seats and the designated
     * seat.
     *
     * @param seatsToBook    The number of seats to be booked.
     * @param designatedSeat The seat designated by the user (if any).
     * @return A 2D array with the coordinates of the planned seats.
     * @throws IllegalArgumentException if the number of seats to book is invalid or
     *                                  if there are not enough available seats.
     */
    public int[][] planSeats(int seatsToBook, String designatedSeat) throws IllegalArgumentException {
        logger.debug("Planning {} seat(s) with designated seat: {}", seatsToBook, designatedSeat);
        if (!checkSeatsMoreThanZero(seatsToBook)) {
            logger.error("Invalid seat count: {}", seatsToBook);
            throw new IllegalArgumentException("Seats to book cannot be negative: " + seatsToBook);
        }
        if (!checkSeatsAvailability(seatsToBook)) {
            logger.error("Not enough seats available. Requested: {}, Available: {}", seatsToBook,
                    seatMap.getAvailableSeats());
            throw new IllegalArgumentException("Not enough seats available to book: " + seatsToBook);
        }
        int[][] plannedCoordinates;
        copySeatMap();
        if (designatedSeat == null || designatedSeat.isEmpty()) {
            logger.info("Planning without designated seat");
            plannedCoordinates = this.planSeatsWithoutDesignatedSeat(seatsToBook);
        } else if (InputValidator.isValidChosenSeat(designatedSeat)) {
            int row = seatMap.getRows() - 1 - (designatedSeat.toUpperCase().charAt(0) - 'A');
            int col = Integer.parseInt(designatedSeat.substring(1)) - 1;
            logger.debug("Parsed designated seat to coordinates: row={}, col={}", row, col);
            if (row < 0 || col >= seatMap.getSeatsPerRow()) {
                logger.error("Designated seat exceeded boundaries: {}", designatedSeat);
                throw new IllegalArgumentException("Designated seat exceeded boundaries: " + designatedSeat);
            }
            plannedCoordinates = this.planSeatsWithDesignatedSeat(seatsToBook, row, col);
        } else {
            logger.error("Invalid designated seat: {}", designatedSeat);
            throw new IllegalArgumentException("Invalid designated seat: " + designatedSeat);
        }
        logger.trace("Planned coordinates: {}", Arrays.deepToString(plannedCoordinates));
        return plannedCoordinates;
    }

    /**
     * Checks if the number of seats to book is greater than zero.
     *
     * @param seatsToBook The number of seats to be booked.
     * @return true if the number of seats to book is greater than zero.
     */
    public boolean checkSeatsMoreThanZero(int seatsToBook) {
        return seatsToBook > 0;
    }

    /**
     * Checks if there are enough available seats to book.
     *
     * @param seatsToBook The number of seats to be booked.
     * @return true if there are enough available seats, false otherwise.
     */
    public boolean checkSeatsAvailability(int seatsToBook) {
        if (seatsToBook > seatMap.getAvailableSeats()) {
            logger.error("Not enough seats available to book: {}", seatsToBook);
            return false;
        }
        return true;
    }

    /**
     * Checks the booking status using the booking ID and updates the seat map
     * accordingly.
     *
     * @param bookingId The booking ID to check.
     * @return A string representation of the updated seat map.
     * @throws IllegalArgumentException if the bookingId is null/empty or cannot be found in booking id cache
     */
    public String checkBooking(String bookingId) throws IllegalArgumentException {
        logger.info("Checking booking for ID: {}", bookingId);
        if (bookingId != null && !bookingId.isEmpty()) {
            int[][] coordinates = getBookingIdCache().get(bookingId);
            if (coordinates != null) {
                logger.debug("Booking ID found, highlighting reserved seats");
                for (int[] coordinate : coordinates) {
                    seatMapPlanArr[coordinate[0]][coordinate[1]] = 2;
                }
            } else {
                logger.error("Booking Id not found: {}", bookingId);
                throw new IllegalArgumentException("Booking ID not found: " + bookingId);
            }
        } else {
            logger.error("Booking Id should not be null or empty");
            throw new IllegalArgumentException("Booking Id should not be null or empty");
        }
        String seatMapPlan = printSeatMapPlan();
        copySeatMap();
        return seatMapPlan;
    }

    /**
     * Plans the seats with a designated seat (starting point for the booking).
     *
     * @param seatsToBook The number of seats to be booked.
     * @param row         The row where the first seat will be booked.
     * @param col         The column where the first seat will be booked.
     * @return A 2D array of coordinates representing the booked seats.
     */
    private int[][] planSeatsWithDesignatedSeat(int seatsToBook, int row, int col) {
        logger.trace("Planning {} seats: at row {} and col {}", seatsToBook, row, col);
        return assignSeats(seatsToBook, row, col);
    }

    /**
     * Plans the seats without a designated seat, starting from the last row.
     *
     * @param seatsToBook The number of seats to be booked.
     * @return A 2D array of coordinates representing the booked seats.
     */
    private int[][] planSeatsWithoutDesignatedSeat(int seatsToBook) {
        logger.trace("Planning {} seats without designated seats", seatsToBook);
        int row = seatMap.getRows() - 1;
        int availableSeats = getAvailableSeatsInRow(row, seatMap.getSeatMapArr());
        int col = seatsToBook < availableSeats ? availableSeats / 2 - seatsToBook / 2 : 0;

        return assignSeats(seatsToBook, row, col);
    }

    /**
     * Assigns seats to the user based on the number of seats, row, and column.
     *
     * @param seatsToBook The number of seats to be booked.
     * @param row         The row where the seats will be booked.
     * @param col         The column where the seats will be booked.
     * @return A 2D array of coordinates representing the booked seats.
     */
    private int[][] assignSeats(int seatsToBook, int row, int col) {
        logger.trace("Assigning {} seat(s) starting at row {}, col {}", seatsToBook, row, col);
        int[] nextAvailableSeat = new int[] { row, col };
        int remainingSeats = seatsToBook;
        int i = 0;
        int[][] coords = new int[seatsToBook][2];
        while (i < seatsToBook) {
            if (this.seatMapPlanArr[nextAvailableSeat[0]][nextAvailableSeat[1]] == 0) {
                this.seatMapPlanArr[nextAvailableSeat[0]][nextAvailableSeat[1]] = 2;
                remainingSeats--;
                coords[i][0] = nextAvailableSeat[0];
                coords[i][1] = nextAvailableSeat[1];
                i++;
                continue;
            }
            nextAvailableSeat = getNextAvailableSeat(nextAvailableSeat[0], nextAvailableSeat[1], remainingSeats,
                    this.seatMapPlanArr);
        }
        return coords;
    }

    /**
     * Gets the next available seat for booking.
     *
     * @param row            The current row.
     * @param col            The current column.
     * @param remainingSeats The number of remaining seats to be booked.
     * @param seats          The current seat map.
     * @return The coordinates of the next available seat.
     */
    private int[] getNextAvailableSeat(int row, int col, int remainingSeats, int[][] seats) {
        logger.trace("Get next available seat");
        int totalColumns = seatMap.getSeatsPerRow();
        if (col < totalColumns - 1) {
            col++;
        } else {
            row = row <= 0 ? seatMap.getRows() - 1 : row - 1;
            int availableSeatsInRow = getAvailableSeatsInRow(row, seats);
            col = remainingSeats < availableSeatsInRow ? availableSeatsInRow / 2 - remainingSeats / 2
                    : 0;
        }
        return new int[] { row, col };
    }

    /**
     * Gets the number of available seats in a specific row.
     *
     * @param row   The row to check for available seats.
     * @param seats The seat map to check.
     * @return The number of available seats in the row.
     */
    private int getAvailableSeatsInRow(int row, int[][] seats) {
        logger.trace("Get number of available seats in row");
        int totalColumns = seatMap.getSeatsPerRow();
        int seatsAvailableInRow = 0;
        for (int j = 0; j < totalColumns; j++) {
            if (seats[row][j] == 0) {
                seatsAvailableInRow++;
            }
        }
        return seatsAvailableInRow;
    }

    /**
     * Creates a copy of the seat map for use in planning the booking.
     */
    private void copySeatMap() {
        logger.debug("Copying seat map array for planning");
        for (int i = 0; i < this.seatMap.getRows(); i++) {
            for (int j = 0; j < this.seatMap.getSeatsPerRow(); j++) {
                this.seatMapPlanArr[i][j] = this.seatMap.getSeatMapArr()[i][j];
            }
        }
    }

    /**
     * Generates the booking ID in the format "GICxxxx", where xxxx is a 4-digit
     * number.
     *
     * @return The generated booking ID.
     */
    public String getBookingId() {
        return BOOKING_ID_PREFIX + String.format("%04d", bookingIdCounter);
    }

    /**
     * Increments the booking ID counter for the next booking.
     */
    private void incrementBookingIdCounter() {
        bookingIdCounter++;
    }

    /**
     * Stores the coordinates of booked seats in the booking ID cache.
     *
     * @param bookingId   The booking ID associated with the seats.
     * @param coordinates The coordinates of the booked seats.
     */
    private void insertBookingIdSeats(String bookingId, int[][] coordinates) {
        logger.debug("Storing booking coordinates for ID: {}", bookingId);
        this.bookingIdCache.put(bookingId, coordinates);
    }

    /**
     * Prints the seat map with the current booking status (booked, reserved, or
     * available seats).
     *
     * @return A string representation of the seat map.
     */
    public String printSeatMapPlan() {
        logger.trace("Incrementing booking ID counter from {}", bookingIdCounter);
        logger.debug("Generating visual representation of the seat map");
        StringBuilder seatMapVisual = new StringBuilder();
        String screenName = "SCREEN";

        int screenLength = this.seatMap.getSeatsPerRow() * 4;
        int padding = (screenLength - screenName.length()) / 2;
        seatMapVisual.append(" ".repeat(padding));
        seatMapVisual.append("SCREEN\n");
        seatMapVisual.append("-".repeat(screenLength));

        seatMapVisual.append("\n");

        for (int i = 0; i < this.seatMap.getRows(); i++) {
            char rowLabel = (char) ('A' + (this.seatMap.getRows() - i - 1));
            seatMapVisual.append(rowLabel + "\s\s");
            for (int j = 0; j < this.seatMap.getSeatsPerRow(); j++) {
                if (seatMapPlanArr[i][j] == 1) {
                    seatMapVisual.append("#\s\s\s");
                } else if ((seatMapPlanArr[i][j] == 2)) {
                    seatMapVisual.append("O\s\s\s");
                } else {
                    seatMapVisual.append(".\s\s\s");
                }
            }
            seatMapVisual.append("\n");
        }

        seatMapVisual.append("\s\s\s");
        for (int j = 1; j <= this.seatMap.getSeatsPerRow(); j++) {
            seatMapVisual.append(j);
            String space = j < 10 ? "\s\s\s" : "\s\s";
            seatMapVisual.append(space);
        }
        seatMapVisual.append("\n");
        return seatMapVisual.toString();
    }

    /**
     * Gets the seat map plan array.
     *
     * @return The seat map plan array.
     */
    public int[][] getSeatMapPlanArr() {
        return this.seatMapPlanArr;
    }

    /**
     * Gets the booking ID cache which holds the coordinates of booked seats.
     *
     * @return The booking ID cache.
     */
    public Map<String, int[][]> getBookingIdCache() {
        return this.bookingIdCache;
    }

    /**
     * Gets the seat map.
     *
     * @return The seat map.
     */
    public SeatMap getSeatMap() {
        return this.seatMap;
    }
}

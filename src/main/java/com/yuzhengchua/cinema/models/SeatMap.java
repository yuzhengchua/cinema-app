package com.yuzhengchua.cinema.models;

/**
 * The SeatMap class represents a seating arrangement for a cinema hall. It maintains
 * the details of the number of rows, seats per row, and the current availability of seats.
 * The seat map is stored in a 2D array where each seat can be marked as available (0) or booked (1).
 * <p>
 * The class provides methods for retrieving and modifying the seating arrangement, as well as tracking
 * the number of available seats.
 * </p>
 * 
 * @since 1.0
 */
public class SeatMap {

    /**
     * The number of rows in the cinema hall.
     */
    private int rows;

    /**
     * The number of seats in each row of the cinema hall.
     */
    private int seatsPerRow;

    /**
     * The number of available seats in the cinema hall.
     */
    private int availableSeats;

    /**
     * A 2D array representing the seat map. Each element is an integer where:
     * 0 represents an available seat, and 1 represents a booked seat.
     */
    private int[][] seatMapArr;

    /**
     * Constructs a new SeatMap with the given number of rows and seats per row.
     * The available seats are calculated as the product of rows and seats per row.
     * The seat map array is initialized with all seats marked as available (0).
     * 
     * @param rows The number of rows in the cinema hall.
     * @param seatsPerRow The number of seats per row in the cinema hall.
     */
    public SeatMap(int rows, int seatsPerRow) {
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.availableSeats = rows * seatsPerRow;
        this.seatMapArr = init();
    }

    /**
     * Initializes the seat map array, setting all seats to available (0).
     * 
     * @return A 2D array representing the seat map where all seats are available.
     */
    private int[][] init() {
        int[][] seatMap = new int[rows][seatsPerRow];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < seatsPerRow; j++) {
                seatMap[i][j] = 0; // 0 indicates available seat
            }
        }
        return seatMap;
    }

    /**
     * Gets the number of rows in the cinema hall.
     * 
     * @return The number of rows in the cinema hall.
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Gets the number of seats per row in the cinema hall.
     * 
     * @return The number of seats per row in the cinema hall.
     */
    public int getSeatsPerRow() {
        return this.seatsPerRow;
    }

    /**
     * Gets the current number of available seats in the cinema hall.
     * 
     * @return The number of available seats.
     */
    public int getAvailableSeats() {
        return this.availableSeats;
    }

    /**
     * Sets the number of available seats in the cinema hall.
     * 
     * @param availableSeats The new number of available seats.
     */
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    /**
     * Gets the 2D array representing the seat map.
     * 
     * @return A 2D array of integers representing the seat map, where 0 indicates available and 1 indicates booked.
     */
    public int[][] getSeatMapArr() {
        return this.seatMapArr;
    }

    /**
     * Sets the seat map array with a new 2D array of seat data.
     * 
     * @param seatMapArr The new 2D array representing the seat map.
     */
    public void setSeatMapArr(int[][] seatMapArr) {
        for (int i = 0; i < seatMapArr.length; i++) {
            for (int j = 0; j < seatMapArr[0].length; j++) {
                this.seatMapArr[i][j] = seatMapArr[i][j];
            }
        }
    }
}

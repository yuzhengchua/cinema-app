package com.yuzhengchua.cinema.service;

public interface BookingService {
    public boolean confirmBooking(int[][] plannedCoordinates) throws IllegalArgumentException;

    public void cancelBooking(String bookingId) throws IllegalArgumentException;

    public int[][] planSeats(int seatsToBook, String designatedSeats) throws IllegalArgumentException;

    public boolean checkSeatsMoreThanZero(int seatsToBook);

    public boolean checkSeatsAvailability(int seatsToBook);

    public String checkBooking(String bookingId) throws IllegalArgumentException;

    public String printSeatMapPlan();

}

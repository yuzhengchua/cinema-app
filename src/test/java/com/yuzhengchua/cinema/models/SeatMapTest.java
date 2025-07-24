package com.yuzhengchua.cinema.models;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.yuzhengchua.cinema.service.BookingServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SeatMapTest {
    SeatMap seatMap;
    BookingServiceImpl bookingService;
    @BeforeAll
    void init() {
        this.seatMap = new SeatMap(2,2);
        this.bookingService = new BookingServiceImpl(seatMap);
    }
    @Test
	void testInit() {
        
        int[][] expectedMap = {{0,0},{0,0}};
        int[][] actualMap = seatMap.getSeatMapArr();

        assertArrayEquals(expectedMap,actualMap);
	}
    @Test
    void testSetAndGetAvailableSeats() {
        seatMap.setAvailableSeats(3);
        assert(seatMap.getAvailableSeats() == 3);
    }
    @Test
    void testSetAndGetSeatMapArr() {
        int[][] newMap = {{1,0},{0,1}};
        seatMap.setSeatMapArr(newMap);
        assertArrayEquals(newMap, seatMap.getSeatMapArr());
    }
    @Test
    void testGetRowsAndSeatsPerRow() {
        assert(seatMap.getRows() == 2);
        assert(seatMap.getSeatsPerRow() == 2);
    }
}

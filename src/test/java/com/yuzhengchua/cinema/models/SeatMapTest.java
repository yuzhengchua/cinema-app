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
}

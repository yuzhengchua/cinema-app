package com.yuzhengchua.cinema.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.yuzhengchua.cinema.models.SeatMap;

class CinemaWorkflowServiceTest {

    @Mock
    private Scanner mockScanner;

    @Mock
    private BookingServiceImpl mockBookingService;

    @Mock
    private SeatMap mockSeatMap;

    private CinemaWorkflowService cinemaWorkflowService;

    private static final String TITLE = "Avengers";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cinemaWorkflowService = new CinemaWorkflowService(mockScanner, mockBookingService, mockSeatMap, TITLE);
    }
    @Test
    void testRunCaseOneWithNonNumericString() {
        
        when(mockScanner.nextLine()).thenReturn("B", "");

        
        cinemaWorkflowService.runCaseOne();

        
        verify(mockScanner, times(2)).nextLine();
        verify(mockBookingService,times(0)).checkSeatsAvailability(anyInt());
        verify(mockBookingService,times(0)).checkSeatsAvailability(anyInt());
    }

    @Test
    void testRunCaseOneWithValidNumericString() {
        
        when(mockScanner.nextLine()).thenReturn("4","\n");
        when(mockBookingService.checkSeatsAvailability(4)).thenReturn(true);
        when(mockBookingService.checkSeatsMoreThanZero(4)).thenReturn(true);
        when(mockBookingService.getBookingId()).thenReturn("GIC0001");
        when(mockBookingService.planSeats(4, "")).thenReturn(new int[][]{{1,1}});



        cinemaWorkflowService.runCaseOne();

        
        verify(mockScanner, times(2)).nextLine();
        verify(mockBookingService,times(1)).checkSeatsAvailability(anyInt());
        verify(mockBookingService,times(1)).checkSeatsMoreThanZero(anyInt());
        verify(mockBookingService,times(1)).getBookingId();
        verify(mockBookingService,times(1)).planSeats(4,"");
    }

    @Test
    void testRunCaseOneWithValidNumericStringButWrongDesignatedSeat() {
        
        when(mockScanner.nextLine()).thenReturn("4","BBB", "B03", "\n");
        when(mockBookingService.checkSeatsAvailability(4)).thenReturn(true);
        when(mockBookingService.checkSeatsMoreThanZero(4)).thenReturn(true);
        when(mockBookingService.getBookingId()).thenReturn("GIC0001");
        when(mockBookingService.planSeats(4, "")).thenReturn(new int[][]{{1,1}});
        when(mockBookingService.planSeats(4, "B03")).thenReturn(new int[][]{{1,1}});



        cinemaWorkflowService.runCaseOne();

        
        verify(mockScanner, times(4)).nextLine();
        verify(mockBookingService,times(1)).checkSeatsAvailability(anyInt());
        verify(mockBookingService,times(1)).checkSeatsMoreThanZero(anyInt());
        verify(mockBookingService,times(1)).getBookingId();
        verify(mockBookingService,times(1)).planSeats(4,"");
        verify(mockBookingService,times(1)).planSeats(4,"B03");
    }

    @Test
    void testRunCaseOneWithTooManySeats() {
        when(mockScanner.nextLine()).thenReturn("4","\n", "\n");
        when(mockBookingService.checkSeatsAvailability(4)).thenReturn(false);
        when(mockBookingService.checkSeatsMoreThanZero(4)).thenReturn(true);

        cinemaWorkflowService.runCaseOne();

        verify(mockScanner, times(2)).nextLine();
        verify(mockBookingService,times(2)).checkSeatsAvailability(anyInt());
        verify(mockBookingService,times(0)).checkSeatsMoreThanZero(anyInt());
    }

    @Test
    void testRunCaseOneWithNegativeSeats() {
        when(mockScanner.nextLine()).thenReturn("-1","\n", "\n");
        when(mockBookingService.checkSeatsAvailability(-1)).thenReturn(true);
        when(mockBookingService.checkSeatsMoreThanZero(-1)).thenReturn(false);

        cinemaWorkflowService.runCaseOne();

        verify(mockScanner, times(2)).nextLine();
        verify(mockBookingService,times(2)).checkSeatsAvailability(anyInt());
        verify(mockBookingService,times(1)).checkSeatsMoreThanZero(anyInt());
    }

    @Test
    void testRunCaseOneOutOfBoundsSeats() {
        when(mockScanner.nextLine()).thenReturn("1","C1", "\n");
        when(mockBookingService.getBookingId()).thenReturn("GIC0001");
        when(mockBookingService.planSeats(1, "")).thenReturn(new int[][]{{1,1}});
        when(mockBookingService.planSeats(1, "C1")).thenThrow(new IllegalArgumentException());
        when(mockBookingService.checkSeatsAvailability(1)).thenReturn(true);
        when(mockBookingService.checkSeatsMoreThanZero(1)).thenReturn(true);


        cinemaWorkflowService.runCaseOne();

        verify(mockScanner, times(3)).nextLine();
        verify(mockBookingService,times(3)).planSeats(anyInt(), anyString());
        verify(mockBookingService,times(1)).confirmBooking(any());

    }

    @Test
    void testRunCaseTwo() {
        HashMap<String,int[][]> bookingIdCache = new HashMap<>();
        bookingIdCache.put("GIC0001", new int[][]{{1,1}});
        
        when(mockBookingService.getBookingIdCache()).thenReturn(bookingIdCache);
        when(mockBookingService.checkBooking("GIC0001")).thenReturn("MOCK");
        when(mockScanner.nextLine()).thenReturn("GIC0001");


        
        cinemaWorkflowService.runCaseTwo();

        
        verify(mockScanner, times(2)).nextLine();
        verify(mockBookingService).checkBooking(anyString()); 
    }

    @Test
    void testRunCaseTwoWithCancellation() {
        HashMap<String,int[][]> bookingIdCache = new HashMap<>();
        bookingIdCache.put("GIC0001", new int[][]{{1,1}});
        
        when(mockBookingService.getBookingIdCache()).thenReturn(bookingIdCache);
        when(mockBookingService.checkBooking("GIC0001")).thenReturn("MOCK");
        when(mockScanner.nextLine()).thenReturn("GIC0001", "Y");
        

        
        cinemaWorkflowService.runCaseTwo();

        
        verify(mockScanner, times(2)).nextLine();
        verify(mockBookingService).cancelBooking(anyString());
        verify(mockBookingService).checkBooking(anyString()); 
    }

    @Test
    void testRunCaseTwoWithNull() {
        HashMap<String,int[][]> bookingIdCache = new HashMap<>();
        bookingIdCache.put("GIC0001", new int[][]{{1,1}});
        
        when(mockBookingService.getBookingIdCache()).thenReturn(bookingIdCache);
        when(mockScanner.nextLine()).thenReturn(null);

        
        cinemaWorkflowService.runCaseTwo();

        
        verify(mockScanner).nextLine();
        verify(mockBookingService, times(0)).checkBooking(anyString());
    }

    @Test
    void testRunCaseTwoWithInvalidBookingId() {
        HashMap<String,int[][]> bookingIdCache = new HashMap<>();
        bookingIdCache.put("GIC0001", new int[][]{{1,1}});
        
        when(mockBookingService.getBookingIdCache()).thenReturn(bookingIdCache);
        when(mockBookingService.checkBooking("GIC0001")).thenReturn("MOCK");
        when(mockScanner.nextLine()).thenReturn("INVALID_ID", "GIC0001", "N");

        
        cinemaWorkflowService.runCaseTwo();

        
        verify(mockScanner, times(3)).nextLine();
        verify(mockBookingService, times(1)).checkBooking(anyString());
    }

    @Test
    void testRunCaseTwoWithException() {
        HashMap<String,int[][]> bookingIdCache = new HashMap<>();
        bookingIdCache.put("GIC0001", new int[][]{{1,1}});
        when(mockBookingService.getBookingIdCache()).thenReturn(bookingIdCache);
        when(mockScanner.nextLine()).thenReturn("GIC0001", "GIC0001");
        when(mockBookingService.checkBooking("GIC0001")).thenThrow(new IllegalArgumentException()).thenReturn("MOCK");

        cinemaWorkflowService.runCaseTwo();

        
        verify(mockScanner, times(3)).nextLine();
        verify(mockBookingService, times(2)).checkBooking(anyString());

    }

}

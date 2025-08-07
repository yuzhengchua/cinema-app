package com.yuzhengchua.cinema.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.yuzhengchua.cinema.models.SeatMap;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceImplTest {
        BookingServiceImpl bookingServiceImpl;
        SeatMap seatMap;

        @BeforeEach
        void init() {
                seatMap = new SeatMap(5, 5);
                bookingServiceImpl = new BookingServiceImpl(seatMap);
        }

        @Test
        void testCheckBookingId() {
                bookingServiceImpl.confirmBooking(planSeats(3, "B02"));
                String screen = printScreen();
                String seatingArrangement = """
                                E  .   .   .   .   .\s\s\s
                                D  .   .   .   .   .\s\s\s
                                C  .   .   .   .   .\s\s\s
                                B  .   O   O   O   .\s\s\s
                                A  .   .   .   .   .\s\s\s
                                   1   2   3   4   5\s\s\s
                                """;
                assertEquals(screen + seatingArrangement,
                                bookingServiceImpl.checkBooking("GIC0001"));

        }

        @Test
        void testCheckInvalidBookingId() {
                try {
                        bookingServiceImpl.checkBooking("");
                } catch (IllegalArgumentException e) {
                        assertEquals("Booking Id should not be null or empty", e.getMessage());
                }

                try {
                        bookingServiceImpl.checkBooking(null);
                } catch (IllegalArgumentException e) {
                        assertEquals("Booking Id should not be null or empty", e.getMessage());
                }

                try {
                        bookingServiceImpl.checkBooking("GIC0002");
                } catch (IllegalArgumentException e) {
                        assertEquals("Booking ID not found: GIC0002", e.getMessage());
                }
        }

        @Test
        void testInvalidPlannedCoordinates() {
                try {
                        bookingServiceImpl.confirmBooking(null);
                } catch (IllegalArgumentException e) {
                        assertEquals("Invalid planned coordinates. Cannot be empty or null", e.getMessage());
                }

                try {
                        bookingServiceImpl.confirmBooking(new int[][] {});
                } catch (IllegalArgumentException e) {
                        assertEquals("Invalid planned coordinates. Cannot be empty or null", e.getMessage());
                }

                try {
                        bookingServiceImpl.confirmBooking(new int[][] { {} });
                } catch (IllegalArgumentException e) {
                        assertEquals("Invalid planned coordinates: []", e.getMessage());
                }

                try {
                        bookingServiceImpl.confirmBooking(new int[][] { { 5, 1 } });
                } catch (IllegalArgumentException e) {
                        assertEquals("Invalid planned coordinates: [5, 1]", e.getMessage());
                }
                try {
                        bookingServiceImpl.confirmBooking(new int[][] { { 1, 5 } });
                } catch (IllegalArgumentException e) {
                        assertEquals("Invalid planned coordinates: [1, 5]", e.getMessage());
                }

        }

        @Test
        void testGetBookingId() {
                String bookingId = bookingServiceImpl.getBookingId();
                assertEquals("GIC0001", bookingId);
        }

        @Test
        void testLargeSeatMapPlan() {
                SeatMap largeSeatMap = new SeatMap(5, 12);
                bookingServiceImpl = new BookingServiceImpl(largeSeatMap);
                StringBuilder seatMapVisual = new StringBuilder();
                String screenName = "SCREEN";
                int screenLength = largeSeatMap.getSeatsPerRow() * 4;
                int padding = (screenLength - screenName.length()) / 2;
                seatMapVisual.append(" ".repeat(padding));
                seatMapVisual.append("SCREEN\n");
                seatMapVisual.append("-".repeat(screenLength));
                seatMapVisual.append("\n");
                String seatingArrangement = """
                                E  .   .   .   .   .   .   .   .   .   .   .   .\s\s\s
                                D  .   .   .   .   .   .   .   .   .   .   .   .\s\s\s
                                C  .   .   .   .   .   .   .   .   .   .   .   .\s\s\s
                                B  .   .   .   .   .   .   .   .   .   .   .   .\s\s\s
                                A  .   .   .   .   .   .   .   .   .   .   .   .\s\s\s
                                   1   2   3   4   5   6   7   8   9   10  11  12\s\s
                                """;
                assertEquals(seatMapVisual + seatingArrangement,
                                bookingServiceImpl.printSeatMapPlan());
        }

        @Test
        void testInvalidDesignatedSeat() {
                try {
                        planSeats(3, "B06");
                } catch (IllegalArgumentException e) {
                        assertEquals("Designated seat exceeded boundaries: B06", e.getMessage());
                }

                try {
                        planSeats(3, "F04");
                } catch (IllegalArgumentException e) {
                        assertEquals("Designated seat exceeded boundaries: F04", e.getMessage());
                }

                try {
                        planSeats(3, "B-1");
                } catch (IllegalArgumentException e) {
                        assertEquals("Invalid designated seat: B-1", e.getMessage());
                }
        }

        @Test
        void testPrintSeatMap() {
                bookingServiceImpl.confirmBooking(planSeats(3, "B02"));
                String screen = printScreen();
                String seatingArrangement = """
                                E  .   .   .   .   .\s\s\s
                                D  .   .   .   .   .\s\s\s
                                C  .   .   .   .   .\s\s\s
                                B  .   #   #   #   .\s\s\s
                                A  .   .   .   .   .\s\s\s
                                   1   2   3   4   5\s\s\s
                                """;
                assertEquals(22, seatMap.getAvailableSeats());
                assertEquals(screen + seatingArrangement,
                                bookingServiceImpl.printSeatMapPlan());

                planSeats(4, "B03");
                seatingArrangement = """
                                E  .   .   .   .   .\s\s\s
                                D  .   .   .   .   .\s\s\s
                                C  .   O   O   O   .\s\s\s
                                B  .   #   #   #   O\s\s\s
                                A  .   .   .   .   .\s\s\s
                                   1   2   3   4   5\s\s\s
                                """;
                assertEquals(22, seatMap.getAvailableSeats());
                assertEquals(screen + seatingArrangement,
                                bookingServiceImpl.printSeatMapPlan());

                planSeats(4, "A03");
                seatingArrangement = """
                                E  .   .   .   .   .\s\s\s
                                D  .   .   .   .   .\s\s\s
                                C  .   .   .   .   .\s\s\s
                                B  .   #   #   #   O\s\s\s
                                A  .   .   O   O   O\s\s\s
                                   1   2   3   4   5\s\s\s
                                """;
                assertEquals(22, seatMap.getAvailableSeats());
                assertEquals(screen + seatingArrangement,
                                bookingServiceImpl.printSeatMapPlan());
        }

        @Test
        void testPlanSeatsWithDefaultSeatPlan() {
                planSeats(3, "");
                assert (seatMap.getAvailableSeats() == 25);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 2, 2, 2, 0 }
                }, bookingServiceImpl.getSeatMapPlanArr());
        }

        @Test
        void testPlanSeatsWithDefaultSeatPlanOverflow() {
                planSeats(10, null);

                assert (seatMap.getAvailableSeats() == 25);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 2, 2, 2, 2, 2 },
                                { 2, 2, 2, 2, 2 }
                }, bookingServiceImpl.getSeatMapPlanArr());
        }

        @Test
        void testPlanSeatsWithDefaultSeatPlanOverflowWithOccupiedSeats() {
                bookingServiceImpl.confirmBooking(planSeats(3, null));
                planSeats(10, null);
                assert (seatMap.getAvailableSeats() == 22);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 2, 2, 2, 0 },
                                { 2, 2, 2, 2, 2 },
                                { 2, 1, 1, 1, 2 }
                }, bookingServiceImpl.getSeatMapPlanArr());
        }

        @Test
        void testPlanSeatsWithDesignatedSeat() {
                planSeats(3, "B03");

                assert (seatMap.getAvailableSeats() == 25);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 2, 2, 2 },
                                { 0, 0, 0, 0, 0 }
                }, bookingServiceImpl.getSeatMapPlanArr());
        }

        @Test
        void testBookSeatsWithInsufficientSeats() {
                int seatsToBook = 30;
                try {
                        planSeats(seatsToBook, null);
                } catch (IllegalArgumentException e) {
                        assertEquals("Not enough seats available to book: " + seatsToBook, e.getMessage());
                }

        }

        @Test
        void testBookSeatsWithNegativeNumber() {
                int seatsToBook = -1;
                try {
                        planSeats(seatsToBook, null);
                } catch (IllegalArgumentException e) {
                        assertEquals("Seats to book cannot be negative: " + seatsToBook, e.getMessage());
                }
        }

        @Test
        void testBookSeatsWithDefaultSeatPlan() {

                bookingServiceImpl.confirmBooking(planSeats(3, null));
                assert (seatMap.getAvailableSeats() == 22);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 1, 1, 1, 0 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testBookSeatsWithDefaultSeatPlanOverflow() {
                bookingServiceImpl.confirmBooking(planSeats(10, null));
                assert (seatMap.getAvailableSeats() == 15);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testBookSeatsWithDefaultSeatPlanOverflowWithOccupiedSeats() {
                bookingServiceImpl.confirmBooking(planSeats(3, null));
                bookingServiceImpl.confirmBooking(planSeats(10, null));
                assert (seatMap.getAvailableSeats() == 12);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 1, 1, 1, 0 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testplanSeatsWithDesignatedSeat() {
                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));

                assert (seatMap.getAvailableSeats() == 22);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 1, 1, 1 },
                                { 0, 0, 0, 0, 0 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testBookSeatsWithRepeatedSameDesignatedSeat() {
                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));
                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));
                assert (seatMap.getAvailableSeats() == 19);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 0, 0, 0 },
                                { 0, 1, 1, 1, 0 },
                                { 0, 0, 1, 1, 1 },
                                { 0, 0, 0, 0, 0 }
                }, seatMap.getSeatMapArr());

                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));
                assert (seatMap.getAvailableSeats() == 16);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 0, 1, 0, 0 },
                                { 1, 1, 1, 1, 1 },
                                { 0, 0, 1, 1, 1 },
                                { 0, 0, 0, 0, 0 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testBookSeatsWithDesignatedAndDefaultPlan() {
                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));
                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));
                bookingServiceImpl.confirmBooking(planSeats(3, "B03"));
                bookingServiceImpl.confirmBooking(planSeats(10, null));
                assert (seatMap.getAvailableSeats() == 6);
                assertArrayEquals(new int[][] {
                                { 0, 0, 0, 0, 0 },
                                { 0, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testBookAllSeatsWithDesignatedSeat() {
                bookingServiceImpl.confirmBooking(planSeats(25, "B03"));
                assert (seatMap.getAvailableSeats() == 0);
                assertArrayEquals(new int[][] {
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 },
                                { 1, 1, 1, 1, 1 }
                }, seatMap.getSeatMapArr());
        }

        @Test
        void testCancelBookingRemovesBookingAndFreesSeats() {
                // Arrange: Book 2 seats
                int[][] seats = bookingServiceImpl.planSeats(2, "A01");
                bookingServiceImpl.confirmBooking(seats);
                String bookingId = bookingServiceImpl.getBookingIdCache().keySet().iterator().next();
                assertNotNull(bookingServiceImpl.getBookingIdCache().get(bookingId));
                int availableBefore = bookingServiceImpl.getSeatMap().getAvailableSeats();
                // Act: Cancel booking
                bookingServiceImpl.cancelBooking(bookingId);
                // Assert: Booking is removed
                assertNull(bookingServiceImpl.getBookingIdCache().get(bookingId));
                // Assert: Seats are available again
                int availableAfter = bookingServiceImpl.getSeatMap().getAvailableSeats();
                assertEquals(availableBefore + 2, availableAfter);
        }

        @Test
        void testCancelNonExistentBookingThrows() {
                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        bookingServiceImpl.cancelBooking("NONEXISTENT");
                });
                assertTrue(exception.getMessage().contains("not found"));
        }

        @Test
        void testCheckBookingAfterCancellationThrows() {
                int[][] seats = bookingServiceImpl.planSeats(1, "A01");
                bookingServiceImpl.confirmBooking(seats);
                String bookingId = bookingServiceImpl.getBookingIdCache().keySet().iterator().next();
                bookingServiceImpl.cancelBooking(bookingId);
                assertThrows(IllegalArgumentException.class, () -> bookingServiceImpl.checkBooking(bookingId));
        }

        public int[][] planSeats(int seatsToBook, String designatedSeat) {
                return bookingServiceImpl.planSeats(seatsToBook, designatedSeat);
        }

        public String printScreen() {
                StringBuilder seatMapVisual = new StringBuilder();
                String screenName = "SCREEN";
                int screenLength = this.seatMap.getSeatsPerRow() * 4;
                int padding = (screenLength - screenName.length()) / 2;
                seatMapVisual.append(" ".repeat(padding));
                seatMapVisual.append("SCREEN\n");
                seatMapVisual.append("-".repeat(screenLength));
                seatMapVisual.append("\n");
                return seatMapVisual.toString();
        }

        @Test
        void testGetBookingIdCache() {
                bookingServiceImpl.confirmBooking(planSeats(1, "B03"));
                assertArrayEquals(new int[][] { { 3, 2 } }, bookingServiceImpl.getBookingIdCache().get("GIC0001"));
        }
}

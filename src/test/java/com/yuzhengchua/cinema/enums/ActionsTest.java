package com.yuzhengchua.cinema.enums;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ActionsTest {
    @Test
    void testEnumValues() {
        assertEquals("[1] Book tickets for ", Actions.ACTION1.toString());
        assertEquals("[2] Check bookings", Actions.ACTION2.toString());
        assertEquals("[3] Exit", Actions.ACTION3.toString());
    }
} 
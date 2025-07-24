package com.yuzhengchua.cinema.constants;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ConstantsTest {
    @Test
    void testConstantValues() {
        assertEquals(1, Constants.MIN_ROWS);
        assertEquals(26, Constants.MAX_ROWS);
        assertEquals(1, Constants.MIN_SEATS_PER_ROW);
        assertEquals(50, Constants.MAX_SEATS_PER_ROW);
    }
    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }
} 
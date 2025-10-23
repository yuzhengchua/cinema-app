package com.yuzhengchua.cinema.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExtraFunctionsTest {
    @Test
    void testAdd() {
        assert(ExtraFunctions.add(2, 3) == 5);
        assert(ExtraFunctions.add(-1, 1) == 0);
    }

    @Test
    void testSubtract() {
        assert(ExtraFunctions.subtract(5, 3) == 2);
        assert(ExtraFunctions.subtract(0, 1) == -1);
    }

    // @Test
    // void testMultiply() {
    //     assert(ExtraFunctions.multiply(4, 3) == 12);
    //     assert(ExtraFunctions.multiply(-1, 5) == -5);
    // }

    // @Test
    // void testDivide() {
    //     assert(ExtraFunctions.divide(10, 2) == 5.0);
    //     assert(ExtraFunctions.divide(7, -1) == -7.0);
    //     try {
    //         ExtraFunctions.divide(5, 0);
    //         assert false; // Should not reach here
    //     } catch (IllegalArgumentException e) {
    //         assert true; // Expected exception
    //     }
    // }
}

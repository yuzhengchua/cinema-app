package com.yuzhengchua.cinema;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CinemaApplicationTest {
    @Test
    void contextLoads() {
        // This will pass if the Spring context loads successfully
    }
    @Test
    void testMain() {
        CinemaApplication.main(new String[]{});
    }
} 
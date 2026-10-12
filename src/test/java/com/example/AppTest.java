
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void messageIsCorrect() {
        assertEquals("Day 1 CI demo is working", App.message());
    }
}


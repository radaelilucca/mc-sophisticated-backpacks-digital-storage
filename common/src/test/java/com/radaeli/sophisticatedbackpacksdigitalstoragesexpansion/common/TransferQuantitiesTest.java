package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TransferQuantitiesTest {
    @Test
    void acceptedAmountIsClampedToOffer() {
        assertEquals(0, TransferQuantities.accepted(16, -1));
        assertEquals(0, TransferQuantities.accepted(16, 0));
        assertEquals(7, TransferQuantities.accepted(16, 7));
        assertEquals(16, TransferQuantities.accepted(16, Long.MAX_VALUE));
        assertEquals(0, TransferQuantities.accepted(-1, 3));
    }

    @Test
    void operationLimitNeverProducesNegativeOrExcessiveAmounts() {
        assertEquals(0, TransferQuantities.bounded(-5, 1000));
        assertEquals(0, TransferQuantities.bounded(5, 0));
        assertEquals(5, TransferQuantities.bounded(5, 1000));
        assertEquals(1000, TransferQuantities.bounded(1200, 1000));
    }

    @Test
    void backendLongCountsSaturateAtIntegerMaximum() {
        assertEquals(0, TransferQuantities.toInt(Long.MIN_VALUE));
        assertEquals(10, TransferQuantities.toInt(10));
        assertEquals(Integer.MAX_VALUE, TransferQuantities.toInt(Long.MAX_VALUE));
    }
}

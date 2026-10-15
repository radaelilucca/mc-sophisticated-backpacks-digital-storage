package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilterSlotGridTest {
    @Test
    void refillUsesScreenCoordinatesWhenTheBackpackHasANonzeroMenuOffset() {
        // Menu origin (200, 30); first filter item is menu-relative (21, 13).
        // The tab control and pointer use the screen grid origin (220, 42).
        var grid = new FilterSlotGrid(220, 42, 3, 9);
        assertEquals(230, grid.labelX(0));
        assertEquals(44, grid.labelY(0));
        assertEquals(0, grid.slotAt(225, 47));
        assertEquals(-1, grid.slotAt(31, 15)); // The old label appeared in the main inventory here.
        assertEquals(266, grid.labelX(8));
        assertEquals(80, grid.labelY(8));
        assertEquals(8, grid.slotAt(265, 83));
    }

    @Test
    void everyIndicatorAndBothEdgesOfItsCellResolveToTheSameFilter() {
        for (var grid : new FilterSlotGrid[] {
                new FilterSlotGrid(220, 42, 3, 9), new FilterSlotGrid(368, 100, 3, 9) }) {
            for (int slot = 0; slot < 9; slot++) {
                int left = grid.x() + slot % 3 * 18;
                int top = grid.y() + slot / 3 * 18;
                assertEquals(slot, grid.slotAt(left, top));
                assertEquals(slot, grid.slotAt(left + 17.99, top + 17.99));
                assertEquals(slot, grid.slotAt(grid.labelX(slot), grid.labelY(slot)));
            }
        }
    }

    @Test
    void inventoryFooterAndMissingCellsCannotChangeAFilterMode() {
        var grid = new FilterSlotGrid(220, 42, 3, 8);
        assertEquals(-1, grid.slotAt(219.99, 42));
        assertEquals(-1, grid.slotAt(220, 41.99));
        assertEquals(-1, grid.slotAt(274, 42));
        assertEquals(-1, grid.slotAt(220, 96));
        assertEquals(-1, grid.slotAt(265, 83));
        assertEquals(-1, grid.slotAt(Double.NaN, 42));
    }
}

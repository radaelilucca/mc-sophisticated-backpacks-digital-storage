package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.common;

/** Native 18-pixel filter cells, expressed entirely in screen coordinates. */
public record FilterSlotGrid(int x, int y, int columns, int slotCount) {
    private static final int CELL_SIZE = 18;

    public FilterSlotGrid {
        if (columns <= 0 || slotCount < 0) {
            throw new IllegalArgumentException("Invalid filter grid dimensions");
        }
    }

    public int slotAt(double mouseX, double mouseY) {
        double relativeX = mouseX - x;
        double relativeY = mouseY - y;
        int rows = (slotCount + columns - 1) / columns;
        if (!Double.isFinite(relativeX) || !Double.isFinite(relativeY)
                || relativeX < 0 || relativeY < 0
                || relativeX >= columns * CELL_SIZE || relativeY >= rows * CELL_SIZE) {
            return -1;
        }
        int slot = (int) (relativeY / CELL_SIZE) * columns + (int) (relativeX / CELL_SIZE);
        return slot < slotCount ? slot : -1;
    }

    public int labelX(int slot) {
        return x + slot % columns * CELL_SIZE + 10;
    }

    public int labelY(int slot) {
        return y + slot / columns * CELL_SIZE + 2;
    }
}

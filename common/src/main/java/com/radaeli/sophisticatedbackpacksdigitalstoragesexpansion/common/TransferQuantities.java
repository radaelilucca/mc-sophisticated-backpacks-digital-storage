package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.common;

/** Quantity arithmetic shared by the Forge and NeoForge storage adapters. */
public final class TransferQuantities {
    private TransferQuantities() {}

    /** Bounds an externally reported accepted amount to the amount actually offered. */
    public static int accepted(int offered, long accepted) {
        if (offered <= 0 || accepted <= 0) {
            return 0;
        }
        return (int) Math.min((long) offered, accepted);
    }

    /** Bounds an amount to a non-negative operation limit. */
    public static int bounded(int amount, int limit) {
        if (amount <= 0 || limit <= 0) {
            return 0;
        }
        return Math.min(amount, limit);
    }

    /** Converts a non-negative backend quantity without overflowing an int. */
    public static int toInt(long amount) {
        if (amount <= 0) {
            return 0;
        }
        return (int) Math.min(amount, Integer.MAX_VALUE);
    }
}

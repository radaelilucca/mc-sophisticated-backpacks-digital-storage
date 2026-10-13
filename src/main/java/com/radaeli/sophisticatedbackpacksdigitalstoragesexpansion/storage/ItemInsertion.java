package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage;

import net.minecraft.world.item.ItemStack;

/**
 * An insertion destination bound to one operation's player and network.
 * Inputs must not be mutated. Simulation is an estimate; insert must return the actual remainder.
 */
public interface ItemInsertion {
    int simulateInsert(ItemStack stack);

    ItemStack insert(ItemStack stack);

    static int acceptedCount(ItemStack offered, long accepted) {
        return (int) Math.min(offered.getCount(), Math.max(0L, accepted));
    }

    static ItemStack remainder(ItemStack offered, long accepted) {
        int remaining = offered.getCount() - acceptedCount(offered, accepted);
        return remaining == 0 ? ItemStack.EMPTY : offered.copyWithCount(remaining);
    }
}

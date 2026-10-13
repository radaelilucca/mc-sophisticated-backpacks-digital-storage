package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public final class EndpointBinding {
    private static final String LINK_TAG = "remote_storage_link";

    private EndpointBinding() {
    }

    public static Optional<StorageEndpoint> read(ItemStack stack) {
        CompoundTag root = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains(LINK_TAG, CompoundTag.TAG_COMPOUND)) return Optional.empty();
        CompoundTag link = root.getCompound(LINK_TAG);
        String backend = link.getString("backend");
        ResourceLocation dimension = ResourceLocation.tryParse(link.getString("dimension"));
        if (backend.isBlank() || dimension == null || !link.contains("pos", CompoundTag.TAG_LONG)) return Optional.empty();
        return Optional.of(new StorageEndpoint(backend, ResourceKey.create(Registries.DIMENSION, dimension),
                BlockPos.of(link.getLong("pos"))));
    }

    public static boolean hasBinding(ItemStack stack) {
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().contains(LINK_TAG);
    }

    public static void write(ItemStack stack, StorageEndpoint endpoint) {
        CompoundTag link = new CompoundTag();
        link.putString("backend", endpoint.backendId());
        link.putString("dimension", endpoint.dimension().location().toString());
        link.putLong("pos", endpoint.pos().asLong());
        CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, tag -> tag.put(LINK_TAG, link));
    }

    public static void clear(ItemStack stack) {
        CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(LINK_TAG));
    }
}

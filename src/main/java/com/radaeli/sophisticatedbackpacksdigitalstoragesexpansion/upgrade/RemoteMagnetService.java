package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.upgrade;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.IItemHandlerSimpleInserter;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;

import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.ItemInsertion;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.SourceLocation;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.StorageAdapterRegistry;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.StorageConnection;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.StorageOperation;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.StorageTarget;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.StorageTransferGuard;

/** Tick-driven magnet behavior. Candidate discovery is bounded by the native advanced Magnet range. */
final class RemoteMagnetService {
    private RemoteMagnetService() {
    }

    static boolean tick(RemoteMagnetUpgradeWrapper wrapper, Player player, Level level, BlockPos origin) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)
                || !player.isAlive() || !wrapper.isEnabled() || !isInstalled(wrapper)) {
            return false;
        }

        ITrackedContentsItemHandler backpack = wrapper.getStorageWrapper().getInventoryForUpgradeProcessing();
        StorageConnection connection = StorageAdapterRegistry.connect(serverPlayer, wrapper.getEndpoint(),
                SourceLocation.carriedBy(serverPlayer), StorageOperation.INSERT);
        StorageTarget remote = connection.target().orElse(null);
        Vec3 center = Vec3.atCenterOf(origin);
        AABB search = new AABB(origin).inflate(wrapper.getRange());
        List<ItemEntity> candidates = level.getEntitiesOfClass(ItemEntity.class, search,
                entity -> entity.isAlive() && !entity.getItem().isEmpty())
                .stream()
                .sorted(Comparator.comparingDouble((ItemEntity entity) -> entity.position().distanceToSqr(center))
                        .thenComparingInt(ItemEntity::getId))
                .toList();

        boolean movedAny = false;
        for (ItemEntity entity : candidates) {
            if (!isStillEligible(wrapper, player, entity)) {
                continue;
            }
            ItemStack offered = entity.getItem().copy();
            if (!wrapper.getFilterLogic().matchesFilter(offered)) {
                continue;
            }

            ItemStack simulatedRemainder = route(wrapper, serverPlayer, backpack, remote, offered.copy(), true);
            if (simulatedRemainder.getCount() >= offered.getCount()) {
                continue;
            }
            if (!isStillEligible(wrapper, player, entity)
                    || !ItemStack.isSameItemSameComponents(entity.getItem(), offered)) {
                continue;
            }

            ItemStack remainder = route(wrapper, serverPlayer, backpack, remote, entity.getItem().copy(), false);
            if (remainder.getCount() == entity.getItem().getCount()) {
                continue;
            }
            entity.setItem(remainder);
            if (remainder.isEmpty()) {
                entity.discard();
            }
            movedAny = true;
        }

        if (movedAny) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP,
                    SoundSource.PLAYERS, 0.2F, 1.0F);
        }
        return movedAny;
    }

    private static ItemStack route(RemoteMagnetUpgradeWrapper wrapper, ServerPlayer player,
            IItemHandlerSimpleInserter backpack, StorageTarget remote, ItemStack offered, boolean simulate) {
        if (offered.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = offered.copy();
        if (wrapper.getSourcePriority() == SourcePriority.NETWORK_FIRST) {
            remainder = insertRemote(wrapper, player, remote, remainder, simulate);
            return insertBackpack(backpack, remainder, simulate);
        }
        remainder = insertBackpack(backpack, remainder, simulate);
        return insertRemote(wrapper, player, remote, remainder, simulate);
    }

    private static ItemStack insertRemote(RemoteMagnetUpgradeWrapper wrapper, ServerPlayer player,
            StorageTarget remote, ItemStack offered, boolean simulate) {
        if (remote == null || offered.isEmpty()) {
            return offered;
        }
        try (StorageTransferGuard ignored = StorageTransferGuard.blockExternalInsertion(wrapper.getStorageWrapper())) {
            if (simulate) {
                int accepted = ItemInsertion.acceptedCount(offered,
                        remote.simulateInsert(offered.copy(), player));
                return ItemInsertion.remainder(offered, accepted);
            }
            return remote.insert(offered.copy(), player);
        }
    }

    private static ItemStack insertBackpack(IItemHandlerSimpleInserter backpack, ItemStack offered,
            boolean simulate) {
        return offered.isEmpty() ? ItemStack.EMPTY : backpack.insertItem(offered.copy(), simulate);
    }

    private static boolean isStillEligible(RemoteMagnetUpgradeWrapper wrapper, Player player, ItemEntity entity) {
        return entity.isAlive() && !entity.getItem().isEmpty() && !entity.hasPickUpDelay()
                && !RemoteMagnetEligibility.isBlocked(entity)
                && !entity.getPersistentData().contains("PreventRemoteMovement")
                && (entity.getTarget() == null || entity.getTarget().equals(player.getUUID()));
    }

    private static boolean isInstalled(RemoteMagnetUpgradeWrapper wrapper) {
        return wrapper.getStorageWrapper().getUpgradeHandler().getSlotWrappers().containsValue(wrapper);
    }
}

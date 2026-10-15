package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.upgrade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeCountLimitConfig;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeGroup;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;

import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.EndpointBinding;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.storage.StorageText;

/** Common item behavior for manually linked upgrades. The Sophisticated upgrade tag is still required. */
public abstract class LinkedStorageUpgradeItem<W extends IUpgradeWrapper> extends UpgradeItemBase<W> {
    protected LinkedStorageUpgradeItem(int maxPerStorage) {
        super(new IUpgradeCountLimitConfig() {
            @Override
            public int getMaxUpgradesPerStorage(String storageType, ResourceLocation upgradeId) {
                return maxPerStorage;
            }

            @Override
            public int getMaxUpgradesInGroupPerStorage(String storageType, UpgradeGroup group) {
                return Integer.MAX_VALUE;
            }
        });
    }

    @Override
    public List<IUpgradeItem.UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    /** Upgrades with persisted reservations must reject rebinding and crafting until recovery completes. */
    public boolean canChangeBinding(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag) {
        // Preserve Sophisticated's translated description lines with the primary tooltip emphasis.
        List<Component> description = new ArrayList<>();
        super.appendHoverText(stack, context, description, flag);
        description.forEach(line -> tooltip.add(line.copy().withStyle(ChatFormatting.GRAY)));

        var endpoint = EndpointBinding.read(stack);
        tooltip.add(getLinkStatus(stack));
        if (endpoint.isPresent()) {
            tooltip.add(Component.translatable("item.sophisticated_backpacks_digital_storages_expansion.linked_upgrade.unlink_hint")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        if (flag.isAdvanced()) {
            endpoint.ifPresent(value -> tooltip.add(StorageText.endpoint("gui.sophisticated_backpacks_digital_storages_expansion.endpoint", value)
                    .copy().withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    /** Recovery details belong to the status, before the conditional unlink instruction. */
    protected Component getLinkStatus(ItemStack stack) {
        return EndpointBinding.read(stack).map(StorageText::tooltipSummary)
                .orElseGet(() -> Component.translatable("gui.sophisticated_backpacks_digital_storages_expansion.not_linked")
                        .withStyle(ChatFormatting.DARK_GRAY));
    }
}

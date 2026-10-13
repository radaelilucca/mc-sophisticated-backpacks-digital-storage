package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.client;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeWrapper.TargetSlot;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton.StateData;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl;

import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.upgrade.RemoteRefillUpgradeContainer;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.upgrade.SourcePriority;

public final class RemoteRefillUpgradeTab extends LinkedStorageUpgradeTab<RemoteRefillUpgradeContainer> {
    private static final Map<TargetSlot, StateData> TARGET_STATES = createTargetStates();
    private final FilterLogicControl<?, ?> filterControl;

    public RemoteRefillUpgradeTab(RemoteRefillUpgradeContainer container, Position position, StorageScreenBase<?> screen) {
        super(container, position, screen,
                Component.translatable("upgrade.sophisticated_backpacks_digital_storages_expansion.remote_refill_upgrade"),
                Component.translatable("upgrade.sophisticated_backpacks_digital_storages_expansion.remote_refill_upgrade.tooltip"));
        filterControl = addHideableChild(new FilterLogicControl<>(screen, new Position(x + 3, y + 24),
                container.getFilterLogicContainer(), 3));
        var targetDefinition = ButtonDefinitions.createSmallToggleButtonDefinition(TARGET_STATES);
        for (int slot = 0; slot < RemoteRefillUpgradeContainer.FILTER_COUNT; slot++) {
            final int filterSlot = slot;
            int col = slot % 3;
            int row = slot / 3;
            ToggleButton<TargetSlot> control = new ToggleButton<>(new Position(
                    filterControl.getX() + filterControl.getWidth() + 5 + col * 14, y + 27 + row * 18),
                    targetDefinition,
                    ignored -> container.setTargetSlot(filterSlot, container.getTargetSlot(filterSlot).next()),
                    () -> container.getTargetSlot(filterSlot)) {
                @Override
                protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                    TargetSlot target = getState.get();
                    String label = switch (target) {
                        case ANY -> "A";
                        case MAIN_HAND -> "M";
                        case OFF_HAND -> "O";
                        default -> target.name().substring("TOOLBAR_".length());
                    };
                    graphics.drawString(font, label, x + (getWidth() - font.width(label)) / 2,
                            y + (getHeight() - font.lineHeight) / 2 + 1, 0xFFFFFFFF, false);
                }
            };
            addHideableChild(control);
        }
        int footerY = filterControl.getY() + filterControl.getHeight() + 6;
        addHideableChild(SourcePriorityControl.create(new Position(x + 5, footerY),
                () -> container.getSourcePriority() == SourcePriority.NETWORK_FIRST, container::toggleSourcePriority));
        addConnectionStatus(25, footerY - y + 5);
    }

    private static Map<TargetSlot, StateData> createTargetStates() {
        Map<TargetSlot, StateData> states = new EnumMap<>(TargetSlot.class);
        for (TargetSlot slot : TargetSlot.values()) {
            states.put(slot, RemoteCheckmark.state(slot.getDescription()));
        }
        return Map.copyOf(states);
    }

    @Override
    protected void moveSlotsToTab() {
        filterControl.moveSlotsToView();
    }
}

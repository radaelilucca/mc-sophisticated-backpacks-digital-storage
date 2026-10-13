package com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.client;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton.StateData;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.alchemy.AlchemyCondition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.alchemy.AlchemyUpgradeTab;

import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.upgrade.RemoteAlchemyUpgradeContainer;
import com.radaeli.sophisticatedbackpacksdigitalstoragesexpansion.upgrade.SourcePriority;

public final class RemoteAlchemyUpgradeTab extends LinkedStorageUpgradeTab<RemoteAlchemyUpgradeContainer> {
    private static final Map<AlchemyCondition, StateData> CONDITION_STATES = createConditionStates();
    private final FilterLogicControl<?, ?> filterControl;

    public RemoteAlchemyUpgradeTab(RemoteAlchemyUpgradeContainer container, Position position, StorageScreenBase<?> screen) {
        super(container, position, screen,
                Component.translatable("upgrade.sophisticated_backpacks_digital_storages_expansion.remote_alchemy_upgrade"),
                Component.translatable("upgrade.sophisticated_backpacks_digital_storages_expansion.remote_alchemy_upgrade.tooltip"));
        filterControl = addHideableChild(new FilterLogicControl<>(screen, new Position(x + 3, y + 24),
                container.getFilterLogicContainer(), 3));
        var conditionDefinition = ButtonDefinitions.createSmallToggleButtonDefinition(CONDITION_STATES);
        for (int slot = 0; slot < 9; slot++) {
            final int filterSlot = slot;
            int col = slot % 3;
            int row = slot / 3;
            addHideableChild(new ToggleButton<>(new Position(x + 4 + col * 18, y + 83 + row * 18),
                    conditionDefinition,
                    mouseButton -> {
                        AlchemyCondition current = container.getCondition(filterSlot);
                        if ((mouseButton == 1 || mouseButton == 2) && current.defaultValue() != 0) {
                            float delta = mouseButton == 1 ? -0.1F : 0.1F;
                            float next = Math.max(0, container.getValue(filterSlot) + delta);
                            container.setConditionValue(filterSlot, current, next);
                        } else {
                            container.setCondition(filterSlot, current.next());
                        }
                    },
                    () -> container.getCondition(filterSlot)));
        }

        addHideableChild(new ToggleButton<>(new Position(x + 4, y + 140), AlchemyUpgradeTab.ANY_EFFECT_MISSING,
                ignored -> container.setMatchAllEffects(!container.shouldMatchAllEffects()),
                container::shouldMatchAllEffects));
        addHideableChild(new ToggleButton<>(new Position(x + 24, y + 140), AlchemyUpgradeTab.MATCH_DURATION,
                ignored -> container.setMatchEffectDuration(!container.shouldMatchEffectDuration()),
                container::shouldMatchEffectDuration));
        addHideableChild(new ToggleButton<>(new Position(x + 44, y + 140), AlchemyUpgradeTab.MATCH_AMPLIFIER,
                ignored -> container.setMatchEffectAmplifier(!container.shouldMatchEffectAmplifier()),
                container::shouldMatchEffectAmplifier));
        addHideableChild(SourcePriorityControl.create(new Position(x + 64, y + 140),
                () -> container.getSourcePriority() == SourcePriority.NETWORK_FIRST, container::toggleSourcePriority));
        addConnectionStatus(25, 145);
    }

    private static Map<AlchemyCondition, StateData> createConditionStates() {
        Map<AlchemyCondition, StateData> states = new EnumMap<>(AlchemyCondition.class);
        for (AlchemyCondition condition : AlchemyCondition.values()) {
            states.put(condition, RemoteCheckmark.state(
                    Component.translatable("gui.sophisticatedcore.alchemy.condition." + condition.getSerializedName())));
        }
        return Map.copyOf(states);
    }

    @Override
    protected void moveSlotsToTab() {
        filterControl.moveSlotsToView();
    }
}

package com.timechanger;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
//? if <26.1 {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?}
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.ClientClockManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import com.timechanger.mixin.ClientClockInstanceAccessor;
//?}
//? if >=1.21.9 <26.1 {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=1.21.9 <26.1 {
import net.minecraft.client.multiplayer.ClientLevel;
//?}

public class TimeChangerClient implements ClientModInitializer {
    private static KeyMapping toggleKey;
    private static boolean locked = false;
    private static long lockedTicks = 1000;

    @Override
    public void onInitializeClient() {
        //? if >=26.1 {
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.timechanger.toggle_time", InputConstants.Type.KEYBOARD, InputConstants.KEY_O, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("timechanger", "main"))));
        //?}
        //? if >=1.21.9 <26.1 {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.timechanger.toggle_time", InputConstants.Type.KEYSYM, InputConstants.KEY_O, KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("timechanger", "main"))));
        //?}
        //? if <1.21.9 {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.timechanger.toggle_time", InputConstants.Type.KEYSYM, InputConstants.KEY_O, "key.category.timechanger.main"));
        //?}
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                if (client.level == null || client.player == null) {
                    break;
                }
                long now = Math.floorMod(daytime(client), 24000);
                lockedTicks = now < 13000 ? 13000 : 1000;
                locked = true;
                //? if >=26.1 {
                client.player.sendSystemMessage(Component.literal(lockedTicks == 1000 ? "Time Changer: Day" : "Time Changer: Night"));
                //?}
                //? if <26.1 {
                client.player.displayClientMessage(Component.literal(lockedTicks == 1000 ? "Time Changer: Day" : "Time Changer: Night"), false);
                //?}
            }
            if (locked && client.level != null) {
                //? if >=26.1 {
                applyClock(client);
                //?}
                //? if >=1.21.9 <26.1 {
                ((ClientLevel.ClientLevelData) client.level.getLevelData()).setDayTime(lockedTicks);
                //?}
                //? if <1.21.9 {
                client.level.setDayTime(lockedTicks);
                //?}
            }
        });
    }

    private static long daytime(Minecraft client) {
        //? if >=26.1 {
        return client.level.getOverworldClockTime();
        //?}
        //? if <26.1 {
        return client.level.getDayTime();
        //?}
    }

    //? if >=26.1 {
    private static void applyClock(Minecraft client) {
        Holder<WorldClock> clock = client.level.registryAccess().getOrThrow(WorldClocks.OVERWORLD);
        ClientClockManager manager = (ClientClockManager) client.level.clockManager();
        ClientClockManager.ClientClockInstance instance = manager.getInstance(clock);
        long current = client.level.getOverworldClockTime();
        long aligned = Math.floorDiv(current, 24000) * 24000 + lockedTicks;
        ClientClockInstanceAccessor accessor = (ClientClockInstanceAccessor) instance;
        accessor.setTotalTicks(aligned);
        accessor.setPartialTick(0f);
        accessor.setRate(0f);
    }
    //?}
}

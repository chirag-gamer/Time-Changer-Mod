//? if >=26.1 {
package com.timechanger.mixin;

import net.minecraft.client.ClientClockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientClockManager.ClientClockInstance.class)
public interface ClientClockInstanceAccessor {
    @Accessor("totalTicks")
    void setTotalTicks(long totalTicks);

    @Accessor("partialTick")
    void setPartialTick(float partialTick);

    @Accessor("rate")
    void setRate(float rate);
}
//?}

package com.leclowndu93150.extrautils2.client.power;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientGpData {
    public static float gpCreated;
    public static float gpDrained;

    public static boolean isPowered() {
        return gpDrained <= gpCreated;
    }

    public static boolean hasNoPower() {
        return gpCreated == 0f && gpDrained == 0f;
    }
}

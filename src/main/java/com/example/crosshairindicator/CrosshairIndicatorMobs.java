package com.example.crosshairindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CrosshairIndicatorMobs implements ClientModInitializer {
    public static final String MOD_ID = "crosshair_indicator_mobs";

    // Ajusta estos valores para cambiar el aspecto (en pixeles de GUI).
    private static final int CROSSHAIR_SIZE = 15; // tamano del sprite de la mira vanilla
    private static final int PAD = -1;             // separacion entre la mira y los corchetes
    private static final int ARM = 3;             // largo de cada segmento de esquina
    private static final int COLOR = 0xFFFFFFFF;  // blanco opaco

    @Override
    public void onInitializeClient() {
        // Se dibuja justo despues de la mira vanilla: no la reemplaza ni la oculta.
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CROSSHAIR,
                Identifier.fromNamespaceAndPath(MOD_ID, "indicator"),
                CrosshairIndicatorMobs::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;

        // hitResult ya lo calcula vanilla (alcance + bloques que tapan). Solo es ENTITY si
        // la entidad es el objetivo mas cercano a lo largo del rayo.
        HitResult hit = mc.hitResult;
        if (!(hit instanceof EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof LivingEntity)) return;

        // Mismo origen que usa vanilla para el sprite de la mira.
        int x0 = (mc.getWindow().getGuiScaledWidth() - CROSSHAIR_SIZE) / 2;
        int y0 = (mc.getWindow().getGuiScaledHeight() - CROSSHAIR_SIZE) / 2;

        int left = x0 - PAD;
        int top = y0 - PAD;
        int right = x0 + CROSSHAIR_SIZE + PAD; // exclusivo
        int bottom = y0 + CROSSHAIR_SIZE + PAD;

        // Esquina superior izquierda
        graphics.fill(left, top, left + ARM, top + 1, COLOR);
        graphics.fill(left, top, left + 1, top + ARM, COLOR);
        // Esquina superior derecha
        graphics.fill(right - ARM, top, right, top + 1, COLOR);
        graphics.fill(right - 1, top, right, top + ARM, COLOR);
        // Esquina inferior izquierda
        graphics.fill(left, bottom - 1, left + ARM, bottom, COLOR);
        graphics.fill(left, bottom - ARM, left + 1, bottom, COLOR);
        // Esquina inferior derecha
        graphics.fill(right - ARM, bottom - 1, right, bottom, COLOR);
        graphics.fill(right - 1, bottom - ARM, right, bottom, COLOR);
    }
}

package com.example.invasionmod.UIOverlayRenderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InvasionPhaseUI {
    // Color Constants
    private static final int BACKGROUND_COLOR = 0xC0000000;  // Semi-transparent black
    private static final int ENEMY_TEXT_COLOR = 0xFFFF4444; // Bright red
    private static final int SHADOW_COLOR = 0x50000000;     // Dark shadow
    private static final int BORDER_COLOR = 0xFFAA0000;     // Golden border

    // UI Layout Constants
// UI Layout Constants
    private static final int PANEL_WIDTH = 240;
    private static final int PANEL_HEIGHT = 52;  // Reduced height
    private static final int PADDING = 10;
    private static final int ICON_SIZE = 32;

    // Resources
    private static final ResourceLocation INVASION_ICON = new ResourceLocation("invasionmod", "textures/gui/invasion_icon.png");

    // Invasion State
    private static float phaseProgress = 0f;

    public InvasionPhaseUI() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            return;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        renderInvasionPanel(guiGraphics, width, height, mc);
    }

    private void renderInvasionPanel(GuiGraphics guiGraphics, int screenWidth, int screenHeight, Minecraft mc) {
        // Calculate panel positioning (anchored to top-right)
        int panelRight = screenWidth - PADDING;
        int panelLeft = panelRight - PANEL_WIDTH;
        int panelTop = PADDING;
        int panelBottom = panelTop + PANEL_HEIGHT;

        // Render shadowed background
        renderPanelBackground(guiGraphics, panelLeft, panelTop, panelRight, panelBottom);

        // Render content
        renderPanelContent(guiGraphics, mc, panelLeft, panelTop);
    }

    private void renderPanelBackground(GuiGraphics guiGraphics, int left, int top, int right, int bottom) {
        // Outer shadow with slight expansion
        guiGraphics.fill(
                left - 2, top - 2,
                right + 2, bottom + 2,
                SHADOW_COLOR
        );

        // Main panel background
        guiGraphics.fill(
                left, top,
                right, bottom,
                BACKGROUND_COLOR
        );

        // Subtle border accent
        guiGraphics.fill(left, top, left + 3, bottom, BORDER_COLOR);
    }

    private void renderPanelContent(GuiGraphics guiGraphics, Minecraft mc, int baseX, int baseY) {
        // Enable blending for icon
        RenderSystem.enableBlend();

        // Render invasion icon
        guiGraphics.blit(
                INVASION_ICON,
                baseX + PADDING,
                baseY + PADDING,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // Render phase information
        guiGraphics.drawString(
                mc.font,
                Component.literal("⚠ Current Phase: " + String.valueOf((int) phaseProgress)),
                baseX + ICON_SIZE + 2 * PADDING,
                baseY + PADDING + ICON_SIZE / 2 - 4,
                ENEMY_TEXT_COLOR
        );
    }

    public static void updateInvasionState(int progress) {
        phaseProgress = progress;
    }
}
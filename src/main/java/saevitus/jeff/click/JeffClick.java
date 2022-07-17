package saevitus.jeff.click;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.util.math.MatrixStack;

public class JeffClick implements ModInitializer {
	// fabric setup shit
	public static final Logger LOGGER = LogManager.getLogger("jeffclick");

	// initialize everything
	private static AutoClick click;
	private static AutoEat eat;
	private boolean isClickActive = false;
	private boolean isEatActive = false;
	final KeyBinding autoClickKey = new KeyBinding("Autoclick", GLFW.GLFW_KEY_UP, "JEFFCLICK (NWORD)");
	final KeyBinding autoEatKey = new KeyBinding("Autoeat", GLFW.GLFW_KEY_DOWN, "JEFFCLICK (NWORD)");


	private void clientStartedEvent(MinecraftClient c) {
		click = new AutoClick(autoClickKey);
		eat =  new AutoEat(autoEatKey);
	}

	private void clientTickEvent(MinecraftClient c) {
		assert c.player != null;
		if (c.world == null) return;

		while (this.autoClickKey.wasPressed()) {
			this.isClickActive = !this.isClickActive;

			click.setActive(this.isClickActive);
			if (!this.isClickActive) {
				click.getKey().setPressed(false);
			}
		}

		while (this.autoEatKey.wasPressed()) {
			this.isEatActive = !this.isEatActive;

			eat.setActive(this.isEatActive);
			if (!this.isEatActive) {
				eat.getKey().setPressed(false);
			}
		}

		if (this.isClickActive) if(click.isActive()) click.doAutoClick(c);
		if (this.isEatActive) if(eat.isActive()) eat.init(c);
	}

	private void renderOverlayEvent(MatrixStack matrix, float delta) {
		Text autoClickText = Text.literal("AUTOCLICKER ACTIVE");
		Text autoEatText = Text.literal("AUTOEAT ACTIVE");

		int clickWidth = MinecraftClient.getInstance().textRenderer.getWidth(autoClickText);
		int eatWidth = MinecraftClient.getInstance().textRenderer.getWidth(autoEatText);
		int y = 10;

		if (this.isClickActive && click.isActive()) {
			int x = (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - (clickWidth / 2);
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix, autoClickText, x, y, 0xFF0000);
		}

		if (this.isEatActive && eat.isActive()) {

			if (click.isActive())
				y += 14;

			int x = (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - (eatWidth / 2);
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix, autoEatText, x, y, 0x00FF00);
		}
	}

	@Override
	public void onInitialize() {
		KeyBindingHelper.registerKeyBinding(this.autoClickKey);
		KeyBindingHelper.registerKeyBinding(this.autoEatKey);

		ClientLifecycleEvents.CLIENT_STARTED.register(this::clientStartedEvent);
		ClientTickEvents.END_CLIENT_TICK.register(this::clientTickEvent);
		HudRenderCallback.EVENT.register(this::renderOverlayEvent);
	}
}

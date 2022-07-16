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
	private boolean isActive = false;
	final KeyBinding key = new KeyBinding("JEFFFFFFF", GLFW.GLFW_KEY_UP, "JEFFCLICK (NWORD)");


	private void clientStartedEvent(MinecraftClient c) {
		click = new AutoClick(key);
		eat =  new AutoEat();
	}

	private void clientTickEvent(MinecraftClient c) {
		assert c.player != null;
		if (c.world == null) return;

		while (this.key.wasPressed()) {
			this.isActive = !this.isActive;
			System.out.println("pressed");
			System.out.println("isActive state: " + this.isActive);

			click.setActive(this.isActive);

			if (!this.isActive) {
				click.getKey().setPressed(false);
			}
		}

		if (this.isActive) {
			if(click.isActive()) {
				click.doAutoClick(c);
				eat.init(c);
			}
		}
	}

	private void renderOverlayEvent(MatrixStack matrix, float delta) {
		if(!click.isActive() || !this.isActive) return;

		Text test = Text.literal("AUTOCLICKER ACTIVE");

		int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(test);
		int x = (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - (textWidth / 2);
		int y = 10;

		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix, test, x, y, 0xFF0000);
	}

	@Override
	public void onInitialize() {
		KeyBindingHelper.registerKeyBinding(this.key);

		ClientLifecycleEvents.CLIENT_STARTED.register(this::clientStartedEvent);
		ClientTickEvents.END_CLIENT_TICK.register(this::clientTickEvent);
		HudRenderCallback.EVENT.register(this::renderOverlayEvent);
	}
}

package saevitus.jeff.click;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.TextRenderer;
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
	public static AutoClick click;
	public static AutoEat eat;
	public boolean isClickActive = false;
	public boolean isEatActive = false;
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
			click.getKey().setPressed(this.isClickActive);
		}

		while (this.autoEatKey.wasPressed()) {
			this.isEatActive = !this.isEatActive;

			eat.setActive(this.isEatActive);
			eat.getKey().setPressed(this.isEatActive);
		}

		if (this.isClickActive && click.isActive())  click.doAutoClick(c);
		if (this.isEatActive && eat.isActive())  eat.init(c);
	}

	@Override
	public void onInitialize() {
		KeyBindingHelper.registerKeyBinding(this.autoClickKey);
		KeyBindingHelper.registerKeyBinding(this.autoEatKey);

		ClientLifecycleEvents.CLIENT_STARTED.register(this::clientStartedEvent);
		ClientTickEvents.END_CLIENT_TICK.register(this::clientTickEvent);

	}
}

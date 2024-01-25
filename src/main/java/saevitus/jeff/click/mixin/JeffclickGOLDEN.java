package saevitus.jeff.click.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import saevitus.jeff.click.JeffClick;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class JeffclickGOLDEN {
	@Inject(method = "render",
			at = @At(value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
			slice = @Slice(from =
			@At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")))
	private void render(MatrixStack matrices, float delta, CallbackInfo info) {
		Text autoClickText = Text.literal("AUTOCLICKER ACTIVE");
		Text autoEatText = Text.literal("AUTOEAT ACTIVE");

		TextRenderer tr = MinecraftClient.getInstance().textRenderer;

		int clickWidth = tr.getWidth(autoClickText);
		int eatWidth = tr.getWidth(autoEatText);
		int text_y = 10;

		matrices.push();
		//matrices.scale(0.5F, 0.5F, 0.5F); // lol
		//matrices.translate(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2, 0, 0);

		if(tr == null)
			return;

		if (JeffClick.click.isActive()) {
			float text_x = (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - (clickWidth / 2);
			//tr.drawWithShadow(matrices, autoClickText, text_x, text_y, 0xFF0000);
		}

		if (JeffClick.eat.isActive()) {
			if (JeffClick.click.isActive()) text_y += 14;

			float text_x = (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - (eatWidth / 2);
			//tr.drawWithShadow(matrices, autoEatText, text_x, text_y, 0x00FF00);
		}

		matrices.pop();

		//HudRenderCallback.EVENT.invoker().onHudRender(matrices, delta);
	}

	/*@Redirect(method = "renderCrosshair",
			at = @At(value = "INVOKE", ordinal = 0,
					target = "Lnet/minecraft/client/gui/hud/InGameHud;" +
							"drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V")
	)
	private void GOLDENmode(InGameHud hud, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
		// golden crosshair
		//RenderSystem.setShaderTexture(0, InGameHud.STATS_ICON_TEXTURE);

		if (JeffClick.click.isActive())
			RenderSystem.setShaderColor(255, 255, 0, 255);

		//hud.drawTexture(matrices, x, y, u, v, width, height);
	}*/
}

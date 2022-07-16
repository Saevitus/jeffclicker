package saevitus.jeff.click;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoClick {
    private final KeyBinding key;
    private boolean active;

    public AutoClick(KeyBinding key) {
        this.key = key;
    }

    public KeyBinding getKey() {
        return this.key;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    //calls the attackEntity function, this just directly causes your player to attack and doesn't play a proper animation
    private void attackEnt(MinecraftClient c) {
        HitResult trace = c.crosshairTarget;

        if (trace instanceof EntityHitResult && c.interactionManager != null)
            c.interactionManager.attackEntity(c.player, ((EntityHitResult) trace).getEntity());
    }

    // everything is considered an entity in minecraft(i think), so this filters it to
    private boolean lookingAtEnt(MinecraftClient c) {
        HitResult trace = c.crosshairTarget;
        if (trace instanceof EntityHitResult) {
            boolean friendly;

            Entity ent = ((EntityHitResult)trace).getEntity();
            friendly = ent instanceof VillagerEntity
                    || ent instanceof TameableEntity
                    || ent instanceof HorseEntity
                    || ent instanceof DonkeyEntity
                    || ent instanceof PlayerEntity
                    || ent instanceof IronGolemEntity
                    || ent instanceof WanderingTraderEntity;

            return ent instanceof LivingEntity && !friendly;
        }

        return false;
    }

    public void doAutoClick(MinecraftClient c) {
        assert c.player != null;
        if (!this.isActive()) return;

        if (!this.lookingAtEnt(c)) {
            if (this.getKey().isPressed()) {
                this.getKey().setPressed(false);
            }
            return;
        }

        if (c.player.getAttackCooldownProgress(0) == 1.0F && this.lookingAtEnt(c)) {
            this.getKey().setPressed(true);
            this.attackEnt(c);
        } else this.getKey().setPressed(false);
    }
}

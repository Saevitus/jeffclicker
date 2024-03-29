package saevitus.jeff.click;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.option.KeyBinding;

public class AutoEat {
    private static MinecraftClient c = null;
    private int oldSlot = -1;

    private final KeyBinding key;
    private boolean active;

    public AutoEat(KeyBinding key) {
        this.key = key;
    }

    public KeyBinding getKey() { return this.key; }

    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }

    public void init(MinecraftClient c) {
        this.c = c;

        OnTick();
    }

    private int GetBestSlot() {
        int slot = -1;
        float largestSat = -1;

        for (int i = 0; i < 9; i++) {
            Item item = this.c.player.getInventory().getStack(i).getItem();

            if (!item.isFood())
                continue;

            FoodComponent food = item.getFoodComponent();

            float sat = food.getSaturationModifier();

            if (sat > largestSat) {
                largestSat = sat;
                slot = i;
            }
        }

        return slot;
    }

    boolean IsClickable(HitResult hr) {
        if (hr == null) return false;

        if (hr instanceof EntityHitResult) {
            Entity ent = ((EntityHitResult)hr).getEntity();
            return ent instanceof VillagerEntity || ent instanceof TameableEntity;
        }

        if (hr instanceof BlockHitResult) {
            BlockPos pos = ((BlockHitResult)hr).getBlockPos();
            if (pos == null) return false;

            Block bl = this.c.world.getBlockState(pos).getBlock();
            return bl instanceof BlockWithEntity || bl instanceof CraftingTableBlock;
        }

        return false;
    }

    public boolean IsEating() {
        return this.oldSlot != -1;
    }

    private void StopEating() {
        if (!IsEating()) return;

        this.c.options.useKey.setPressed(false);

        this.c.player.getInventory().selectedSlot = oldSlot;
        this.oldSlot = -1;
    }

    public void OnTick() {
        if (!this.isActive()) return;

        if (this.c.player.getAbilities().creativeMode
                || !this.c.player.canConsume(false)
                || IsClickable(this.c.crosshairTarget)) {
            StopEating();
            return;
        }

        int bestSlot = GetBestSlot();
        if (bestSlot == -1) {
            StopEating();
            return;
        }

        // save old slot
        if (!IsEating())
            this.oldSlot = this.c.player.getInventory().selectedSlot;

        // set slot
        this.c.player.getInventory().selectedSlot = bestSlot;

        // eat food
        this.c.options.useKey.setPressed(true);
        this.c.interactionManager.interactItem(c.player, Hand.MAIN_HAND);

    }
}

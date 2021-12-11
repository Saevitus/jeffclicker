package saevitus.jeff.click;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.MinecraftClient;
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

public class AutoEat {
    private static MinecraftClient c = null;
    private int oldSlot = -1;

    public void init(MinecraftClient c) {
        this.c = c;

        OnTick();
    }

    private int GetBestSlot() {
        int bestSlot = -1;
        FoodComponent bestFood = null;

        for (int i = 0; i < 9; i++) {
            Item item = this.c.player.getInventory().getStack(i).getItem();

            if (!item.isFood())
                continue;

            FoodComponent food = item.getFoodComponent();

            if (bestFood == null) {
                bestFood = food;
                bestSlot = i;
            }
        }

        return bestSlot;
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

        this.c.options.keyUse.setPressed(false);

        this.c.player.getInventory().selectedSlot = oldSlot;
        this.oldSlot = -1;
    }

    public void OnTick() {
        if (this.c.player.getAbilities().creativeMode
                || !this.c.player.canConsume(false)
                || IsClickable(this.c.crosshairTarget)) {
            StopEating();
            return;
        }

        int bestSlot = GetBestSlot();
        if (bestSlot == -1)
        {
            StopEating();
            return;
        }

        // save old slot
        if (!IsEating())
            this.oldSlot = this.c.player.getInventory().selectedSlot;

        // set slot
        this.c.player.getInventory().selectedSlot = bestSlot;

        // eat food
        this.c.options.keyUse.setPressed(true);
        this.c.interactionManager.interactItem(c.player, c.world, Hand.MAIN_HAND);

    }
}

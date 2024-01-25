package saevitus.jeff.click;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AutoTorch {
    private static MinecraftClient c = null;

    private final KeyBinding key;
    private boolean active;

    public AutoTorch(KeyBinding key) {
        this.key = key;
    }

    public KeyBinding getKey() { return this.key; }

    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }

    static final ImmutableSet<Item> TorchSet = ImmutableSet.of(Items.TORCH, Items.SOUL_TORCH);

    public void init(MinecraftClient c) {
        this.c = c;

        this.tick(c);
    }

    public void tick(MinecraftClient client) {
        if (!isActive()) return;

        if(this.c.player != null && client.world != null) {
            if (!TorchSet.contains(client.player.getOffHandStack().getItem()))
                return;

            BlockPos playerBlock = client.player.getBlockPos();

            if (client.world.getLightLevel(LightType.BLOCK, playerBlock) < 6
                    && canPlaceTorch(playerBlock)) {
                clickBlock(playerBlock);
            }
        }
    }

    private boolean clickBlock(BlockPos pos) {
        Vec3d vec = Vec3d.ofBottomCenter(pos);

        if (isActive()) {
            PlayerMoveC2SPacket.LookAndOnGround packet =
                    new PlayerMoveC2SPacket.LookAndOnGround(this.c.player.getYaw(),
                            90.0F, true);

            this.c.player.networkHandler.sendPacket(packet);
        }

        ActionResult place = this.c.interactionManager.interactBlock(this.c.player,Hand.OFF_HAND,
                new BlockHitResult(vec, Direction.DOWN, pos, false));

        ActionResult hasTorch = this.c.interactionManager.interactItem(this.c.player,Hand.OFF_HAND);

        return (place.isAccepted() && hasTorch.isAccepted());
    }
    public boolean canPlaceTorch(BlockPos pos) {
        return (this.c.world.getBlockState(pos).getFluidState().isEmpty() &&
                Block.sideCoversSmallSquare(this.c.world, pos.down(), Direction.UP));
    }
}
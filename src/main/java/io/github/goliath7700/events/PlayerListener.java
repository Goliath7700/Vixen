package io.github.goliath7700.events;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class PlayerListener {
    private static final Logger log = LoggerFactory.getLogger(PlayerListener.class);

    public static void onPlayerPickUp(PickupItemEvent event) {
        Player player = (Player) event.getLivingEntity();
        player.getInventory().addItemStack(event.getItemStack());
        event.getItemEntity().remove();
    }
    public static void onPlayerBlockInteract(PlayerBlockInteractEvent event) {
        if (event.getPlayer().getItemInMainHand() != ItemStack.AIR) return;
        if (event.getHand() != PlayerHand.MAIN) return;

        log.info("Click!");

        event.getInstance().setBlock(event.getBlockPosition(), Block.AIR);

        Entity fallingBlock = new Entity(EntityType.FALLING_BLOCK);
        FallingBlockMeta fallingBlockMeta = (FallingBlockMeta) fallingBlock.getEntityMeta();
        fallingBlockMeta.setBlock(event.getBlock());

        fallingBlock.setInstance(event.getInstance(), event.getBlockPosition().add(0.5, 0, 0.5));
        fallingBlock.setVelocity(new Vec(0, 10, 0));


        fallingBlock.scheduler().submitTask(() -> {
            BlockVec position = fallingBlock.getPosition().asBlockVec();
            assert fallingBlock.getInstance() != null;
            Block blockBelow = fallingBlock.getInstance().getBlock(
                    position.blockX(), position.blockY() - 1, position.blockZ()
            );

            if (fallingBlock.isOnGround()) {
                log.info(fallingBlockMeta.getBlock().toString());
                Block block = fallingBlockMeta.getBlock();
                fallingBlock.getInstance().setBlock(position, block);
                fallingBlock.remove();
                return TaskSchedule.stop();
            }
            return TaskSchedule.nextTick();
        });
    }
    public static void onPlayerFallingBlockHit(EntityAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Entity target = event.getTarget();
        Vec direction = player.getPosition().direction();
        target.setVelocity(direction.mul(player.getVelocity().add(20, 30, 20)));
        log.info("Falling block hit!");
    }
    public static void onPlayerFallingBlockInteract(PlayerEntityInteractEvent event) {
        if (event.getTarget().getEntityType() != EntityType.FALLING_BLOCK) return;
        if (event.getHand() != PlayerHand.MAIN) return;

        log.info("Falling block interact!");
        Entity fallingBlock = event.getTarget();
        Instance instance = event.getInstance();
        FallingBlockMeta blockMeta = (FallingBlockMeta) fallingBlock.getEntityMeta();

        instance.setBlock(fallingBlock.getPosition().asBlockVec(), blockMeta.getBlock());
        fallingBlock.remove();
    }
}

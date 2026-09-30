package io.github.goliath7700.events;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerListener {
    private static final Logger log = LoggerFactory.getLogger(PlayerListener.class);

    public static void onPlayerPickUp(PickupItemEvent event) {
        Player player = (Player) event.getLivingEntity();
        player.getInventory().addItemStack(event.getItemStack());
        event.getItemEntity().remove();
    }
    public static void onPlayerBlockInteract(PlayerBlockInteractEvent event) {
        if (event.getPlayer().getItemInMainHand() != ItemStack.AIR) {
            return;
        }

        event.getInstance().setBlock(event.getBlockPosition(), Block.AIR);

        Entity fallingBlock = new Entity(EntityType.FALLING_BLOCK);
        FallingBlockMeta fallingBlockMeta = (FallingBlockMeta) fallingBlock.getEntityMeta();
        fallingBlockMeta.setBlock(event.getBlock());

        fallingBlock.setInstance(event.getInstance(), event.getBlockPosition().add(0.5, 0, 0.5));
        fallingBlock.setVelocity(new Vec(0, 10, 0));
        fallingBlock.scheduler().submitTask(() -> {
            if (!fallingBlock.isOnGround()) {
                return TaskSchedule.nextTick();
            }

            event.getInstance().setBlock(fallingBlock.getPosition(), fallingBlockMeta.getBlock());
            fallingBlock.remove();

            return TaskSchedule.stop();
        });
    }
    public static void onPlayerFallingBlockHit(EntityAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Entity target = event.getTarget();
        Vec direction = player.getPosition().direction();
        target.setVelocity(direction.mul(player.getVelocity().add(20, 30, 20)));
    }
//    public static void onPlayerFallingBlockInteract(PlayerEntityInteractEvent event) {
//        if (!(event.getTarget().getEntityType() == EntityType.FALLING_BLOCK)) {
//            return;
//        }
//        final Set<Player> active = new HashSet<>();
//        Entity target = event.getTarget();
//        Player player = event.getPlayer();
//
//        MinecraftServer.getSchedulerManager().buildTask(() -> {
//            for ( player : active) {
//                target.setNoGravity(true);
//                Pos eyePos = player.getPosition().add(0, player.getEyeHeight(), 0);
//                Vec direction = player.getPosition().direction();
//
//                Pos targetPos = eyePos.add(direction.mul(player.getEyeHeight()));
//                Pos velocity = target.getPosition().sub(targetPos);
//                target.setVelocity(Vec.fromPoint(velocity));
//            }
//        }).repeat(TaskSchedule.tick(1)).schedule();
//    }
}

package ch.nerdin.minecraft.plugins.wand.wands;

import ch.nerdin.minecraft.plugins.wand.MagicWand;
import ch.nerdin.minecraft.plugins.wand.Wand;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by edewit on 24/4/16.
 */
public class LeapWand implements Wand {
    public static WeakHashMap<UUID, BukkitTask> leapers = new WeakHashMap<>();

    @Override
    public String getDisplayName() {
        return "Leap Wand";
    }

    @Override
    public void onLeftClick(Player player, PlayerInteractEvent e) {
        player.setVelocity(player.getLocation().getDirection().multiply(6));

        if(leapers.containsKey(player.getUniqueId())) {
            leapers.get(player.getUniqueId()).cancel();
        }

        final BukkitTask autoCancel = Bukkit.getScheduler().runTaskLater(MagicWand.getInstance(), () -> {
            if(leapers.containsKey(player.getUniqueId())) {
                leapers.get(player.getUniqueId()).cancel();
                leapers.remove(player.getUniqueId());
            }
        }, 100L);

        leapers.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(MagicWand.getInstance(), () -> {
            if(player.isOnGround() || player.isFlying() || player.isDead()) {
                leapers.get(player.getUniqueId()).cancel();
                leapers.remove(player.getUniqueId());
                autoCancel.cancel();
            }
            player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 10);
            player.getWorld().playEffect(player.getEyeLocation(), Effect.ENDER_SIGNAL, 10);

            player.getWorld().playEffect(player.getLocation(), Effect.EXTINGUISH, 10);
            player.getWorld().playEffect(player.getEyeLocation(), Effect.EXTINGUISH, 10);
        }, 5L, 2L));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && player.getItemInHand().getItemMeta() != null
                    && player.getItemInHand().getItemMeta().getDisplayName().equals(getDisplayName())) {
                event.setCancelled(true);
            }
        }
    }

}

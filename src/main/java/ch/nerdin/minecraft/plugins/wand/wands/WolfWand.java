package ch.nerdin.minecraft.plugins.wand.wands;

import ch.nerdin.minecraft.plugins.wand.MagicWand;
import ch.nerdin.minecraft.plugins.wand.Wand;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Created by edewit on 28/4/16.
 */
public class WolfWand implements Wand {
    public static WeakHashMap<UUID, List<Wolf>> wolfs = new WeakHashMap<>();
    private static final int PACK_SIZE = 15;

    @Override
    public String getDisplayName() {
        return "Wolf Wand";
    }

    @Override
    public void onLeftClick(Player player, PlayerInteractEvent e) {
        removeWolfs(player);
        final Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(snowball.getVelocity().multiply(2));
    }

    private void removeWolfs(Player player) {
        if (wolfs.containsKey(player.getUniqueId())) {
            List<Wolf> wolfList = WolfWand.wolfs.get(player.getUniqueId());
            wolfList.forEach(Wolf::remove);
            wolfs.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball)) {
            return;
        }
        Snowball snowball = (Snowball) e.getEntity();
        Player player = (Player) snowball.getShooter();

        if (player.getItemInHand() == null || player.getItemInHand().getItemMeta() == null
                || !getDisplayName().equals(player.getItemInHand().getItemMeta().getDisplayName())) {
            return;
        }

        List<Wolf> wolfList = new ArrayList<>(PACK_SIZE);
        for (Entity entity : snowball.getNearbyEntities(7D, 7D, 7D)) {
            if (!(entity instanceof Player)) {
                return;
            }
            Player target = (Player) entity;
            if (target.equals(snowball.getShooter())) {
                continue;
            }

            for (int i = 0; i < PACK_SIZE; i++) {
                Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);
                wolf.setAdult();
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 2));
                wolf.setAngry(true);
                wolf.setTarget(target);
                wolfList.add(wolf);
            }
        }

        wolfs.put(player.getUniqueId(), wolfList);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(MagicWand.getInstance(), () -> {
            for (Iterator<Wolf> iterator = wolfList.iterator(); iterator.hasNext(); ) {
                Wolf wolf = iterator.next();
                if (!wolf.isDead()) {
                    wolf.getWorld().playEffect(wolf.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                } else {
                    iterator.remove();
                }
            }
        }, 0L, 5L);

        Bukkit.getScheduler().runTaskLater(MagicWand.getInstance(), () -> {
            removeWolfs(player);
            task.cancel();
        }, 1000L);
    }
}

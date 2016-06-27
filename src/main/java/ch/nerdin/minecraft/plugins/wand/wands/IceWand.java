package ch.nerdin.minecraft.plugins.wand.wands;

import ch.nerdin.minecraft.plugins.wand.InstantFirework;
import ch.nerdin.minecraft.plugins.wand.MagicWand;
import ch.nerdin.minecraft.plugins.wand.Wand;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by edewit on 28/4/16.
 */
public class IceWand implements Wand {
    public static WeakHashMap<UUID, BukkitTask> snowBalls = new WeakHashMap<>();

    @Override
    public String getDisplayName() {
        return "Encapsulate players in Ice Wand";
    }

    @Override
    public void onLeftClick(Player player, PlayerInteractEvent e) {
        final Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(snowball.getVelocity().multiply(2));

        if (snowBalls.containsKey(player.getUniqueId())) {
            snowBalls.get(player.getUniqueId()).cancel();
        }

        final BukkitTask autoCancel = Bukkit.getScheduler().runTaskLater(MagicWand.getInstance(), () -> {
            if (snowBalls.containsKey(player.getUniqueId())) {
                snowBalls.get(player.getUniqueId()).cancel();
                snowBalls.remove(player.getUniqueId());
            }
        }, 100L);


        snowBalls.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(MagicWand.getInstance(), () -> {
            if (player.isOnGround() || player.isFlying() || player.isDead()) {
                snowBalls.get(player.getUniqueId()).cancel();
                snowBalls.remove(player.getUniqueId());
                autoCancel.cancel();
            }

            InstantFirework.createFireworkEffect(
                    FireworkEffect.builder().flicker(false)
                            .trail(true).with(FireworkEffect.Type.BALL)
                            .withColor(Color.AQUA).withFade(Color.BLACK).build(), snowball.getLocation());
        }, 5L, 2L));

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball) {
            Snowball s = (Snowball) e.getEntity();

            for (Entity entity : s.getNearbyEntities(7D, 7D, 7D)) {
                if (!(entity instanceof Player)) {
                    return;
                }
                Player player = (Player) entity;
                if (player == s.getShooter()) {
                    return;
                }
                player.sendMessage(String.valueOf(ChatColor.AQUA) + ChatColor.BOLD + "You have been frozen, dig to escape!");

                for (Location location : circle(player.getLocation(), 3, 3, false, true, 0)) {
                    location.getBlock().setType(Material.ICE);
                }
            }
        }
    }

    private List<Location> circle(Location loc, int radius, int height, boolean hollow, boolean sphere, int plusY) {
        List<Location> circleBlocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = sphere ? cy - radius : cy; y < (sphere ? cy + radius : cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < (double) (radius * radius) && (!hollow || dist >= (double) ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(loc.getWorld(), x, y + plusY, z);
                        circleBlocks.add(l);
                    }
                }

            }

        }

        return circleBlocks;
    }
}

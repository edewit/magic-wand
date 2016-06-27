package ch.nerdin.minecraft.plugins.wand.wands;

import ch.nerdin.minecraft.plugins.wand.Wand;
import javafx.scene.paint.Color;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by edewit on 24/4/16.
 */
public class PoisonWand implements Wand {

    @Override
    public String getDisplayName() {
        return "Poison Wand";
    }

    @Override
    public void onLeftClick(Player player, PlayerInteractEvent event) {
        List<LivingEntity> entities = getEntitiesInFront(player);
        player.sendMessage(ChatColor.GOLD + "Poisoned " + toString(entities));
        for (LivingEntity entity : entities) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 6000, 1));
        }
    }

    private String toString(List<LivingEntity> list) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<LivingEntity> iterator = list.iterator(); iterator.hasNext(); ) {
            Player livingEntity = (Player) iterator.next();
            sb.append(livingEntity.getName());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private List<LivingEntity> getEntitiesInFront(Player observer) {
        Location observerPos = observer.getEyeLocation();
        Vector3D observerDir = new Vector3D(observerPos.getDirection());

        Vector3D observerStart = new Vector3D(observerPos);
        Vector3D observerEnd = observerStart.add(observerDir.multiply(300));

        List<LivingEntity> targets = new ArrayList<>();
        for (Player target : observer.getWorld().getPlayers()) {
            // Bounding box of the given player
            Vector3D targetPos = new Vector3D(target.getLocation());
            Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
            Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

            if (target != observer && hasIntersection(observerStart, observerEnd, minimum, maximum)) {
                targets.add(target);
            }
        }
        return targets;
    }

    private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;

        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x)
            return false;
        if (Math.abs(c.y) > e.y + ad.y)
            return false;
        if (Math.abs(c.z) > e.z + ad.z)
            return false;

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
            return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
            return false;
        if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
            return false;

        return true;
    }
}

class Vector3D {
    /**
     * Represents the null (0, 0, 0) origin.
     */
    public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);

    // Use protected members, like Bukkit
    public final double x;
    public final double y;
    public final double z;

    /**
     * Construct an immutable 3D vector.
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct an immutable floating point 3D vector from a location object.
     * @param location - the location to copy.
     */
    public Vector3D(Location location) {
        this(location.toVector());
    }

    /**
     * Construct an immutable floating point 3D vector from a mutable Bukkit vector.
     * @param vector - the mutable real Bukkit vector to copy.
     */
    public Vector3D(Vector vector) {
        if (vector == null)
            throw new IllegalArgumentException("Vector cannot be NULL.");
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    /**
     * Adds the current vector and a given position vector, producing a result vector.
     * @param other - the other vector.
     * @return The new result vector.
     */
    public Vector3D add(Vector3D other) {
        if (other == null)
            throw new IllegalArgumentException("other cannot be NULL");
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Adds the current vector and a given vector together, producing a result vector.
     * @param x - the other vector.
     * @return The new result vector.
     */
    public Vector3D add(double x, double y, double z) {
        return new Vector3D(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Substracts the current vector and a given vector, producing a result position.
     * @param other - the other position.
     * @return The new result position.
     */
    public Vector3D subtract(Vector3D other) {
        if (other == null)
            throw new IllegalArgumentException("other cannot be NULL");
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiply each dimension in the current vector by the given factor.
     * @param factor - multiplier.
     * @return The new result.
     */
    public Vector3D multiply(int factor) {
        return new Vector3D(x * factor, y * factor, z * factor);
    }

    /**
     * Multiply each dimension in the current vector by the given factor.
     * @param factor - multiplier.
     * @return The new result.
     */
    public Vector3D multiply(double factor) {
        return new Vector3D(x * factor, y * factor, z * factor);
    }

    /**
     * Retrieve the absolute value of this vector.
     * @return The new result.
     */
    public Vector3D abs() {
        return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    @Override
    public String toString() {
        return String.format("[x: %s, y: %s, z: %s]", x, y, z);
    }
}

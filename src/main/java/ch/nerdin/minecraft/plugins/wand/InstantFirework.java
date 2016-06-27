package ch.nerdin.minecraft.plugins.wand;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class InstantFirework {

    private InstantFirework() {
    }

    public static void createFireworkEffect(FireworkEffect fe, Location loc) {
        Firework f = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(fe);
        f.setFireworkMeta(fm);
        try {
            Class entityFireworkClass = getNMSClass("EntityFireworks");
            Class craftFireworkClass = getOBCClass("entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle", new Class[0]);
            Object entityFirework = handle.invoke(firework, new Object[0]);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Class getNMSClass(String nmsClassString)
            throws ClassNotFoundException {
        String version = (new StringBuilder(String.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]))).append(".").toString();
        String name = (new StringBuilder("net.minecraft.server.")).append(version).append(nmsClassString).toString();
        Class nmsClass = Class.forName(name);
        return nmsClass;
    }

    private static Class getOBCClass(String nmsClassString)
            throws ClassNotFoundException {
        String version = (new StringBuilder(String.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]))).append(".").toString();
        String name = (new StringBuilder("org.bukkit.craftbukkit.")).append(version).append(nmsClassString).toString();
        Class nmsClass = Class.forName(name);
        return nmsClass;
    }
}

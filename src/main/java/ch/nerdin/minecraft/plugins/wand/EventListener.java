package ch.nerdin.minecraft.plugins.wand;

import ch.nerdin.minecraft.plugins.wand.events.CastSpellEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by edewit on 24/4/16.
 */
public class EventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            Wand wand = findWand(e.getItem());
            if (wand != null)
                if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    CastSpellEvent event = new CastSpellEvent(e.getPlayer(), wand);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        wand.onLeftClick(e.getPlayer(), e);
                    }

                    e.setCancelled(true);
                }
        }
    }

    private Wand findWand(ItemStack item) {
        Set<Class<? extends Wand>> wands = MagicWand.getInstance().getAllWands();
        for (Class<? extends Wand> wandClass : wands) {
            try {
                Wand wand = wandClass.newInstance();
                if (wand.getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                    return wand;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}

package ch.nerdin.minecraft.plugins.wand;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by edewit on 24/4/16.
 */
public interface Wand extends Listener {

    String getDisplayName();
    void onLeftClick(Player p, PlayerInteractEvent e);
}

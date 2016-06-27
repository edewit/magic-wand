package ch.nerdin.minecraft.plugins.wand.events;

import ch.nerdin.minecraft.plugins.wand.Wand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CastSpellEvent extends Event implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Wand wand;

    public CastSpellEvent(Player p, Wand wand) {
        this.player = p;
        this.wand = wand;
    }

    public Player getPlayer() {
        return player;
    }

    public Wand getWand() {
        return wand;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

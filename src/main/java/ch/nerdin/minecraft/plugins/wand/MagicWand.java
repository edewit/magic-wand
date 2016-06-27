package ch.nerdin.minecraft.plugins.wand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by edewit on 24/4/16.
 */
public class MagicWand extends JavaPlugin {

    public static final String PREFIX = "ch.nerdin.minecraft.plugins.wand.wands";
    private static MagicWand instance;

    public MagicWand() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
            return false;
        }

        if (command.getName().equalsIgnoreCase("wand")) {
            String arg = (args.length == 0) ? "info" : args[0];

            if ("info".equalsIgnoreCase(arg)) {
                Set<Class<? extends Wand>> wands = getAllWands();
                StringBuffer sb = new StringBuffer();
                for (Class<? extends Wand> wand : wands) {
                    sb.append(" ").append(wand.getSimpleName());
                }
                sender.sendMessage(ChatColor.RED + "use one of wands available " + sb.toString());
            } else {
                try {
                    Class<?> wandClass = Class.forName(PREFIX + "." + arg);
                    Wand wand = (Wand) wandClass.newInstance();
                    giveWand((Player) sender, wand);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "No wand found with name: " + arg);
                }
            }

            return true;
        }
        return false;
    }

    public Set<Class<? extends Wand>> getAllWands() {
        Reflections reflections = new Reflections(PREFIX);
        return reflections.getSubTypesOf(Wand.class);
    }

    private void giveWand(Player player, Wand wand) {
        Material material = Material.BLAZE_ROD;
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        itemMeta.setDisplayName(wand.getDisplayName());
        Bukkit.getPluginManager().registerEvents(wand, this);
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
        player.sendMessage(ChatColor.YELLOW
                + ChatColor.translateAlternateColorCodes('&', String.format("You've received the &b%s&e!", wand.getDisplayName())));
    }

    public static MagicWand getInstance() {
        return instance;
    }
}

package me.darkolythe.shulkerpacks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class ShulkerPacks extends JavaPlugin {

    ShulkerListener shulkerlistener;

    private static ShulkerPacks plugin;

    final String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "에고버스" + ChatColor.WHITE + "] ";

    static Map<Player, ItemStack> openshulkers = new HashMap<>();
    Map<Player, Boolean> fromhand = new HashMap<>();
    Map<Player, Inventory> openinventories = new HashMap<>();
    Map<Player, Inventory> opencontainer = new HashMap<>();
    private Map<Player, Long> pvp_timer = new HashMap<>();
    final PluginSettings settings = PluginSettings.INSTANCE;

    /*
    Sets up the plugin
     */
    @Override
    public void onEnable() {
        plugin = this;
        shulkerlistener = new ShulkerListener(this);

        getServer().getPluginManager().registerEvents(shulkerlistener, this);
        getServer().getPluginManager().registerEvents(new PortableWorkstationListener(this), this);

        getCommand("편의기능").setExecutor(new CommandReload());

        //Metrics metrics = new Metrics(this);

        shulkerlistener.checkIfValid();

        getLogger().log(Level.INFO, (prefix + ChatColor.GREEN + "편의기능이 활성화되었습니다!"));
    }

    /*
    Doesnt do much. Just says a message
     */
    @Override
    public void onDisable() {
        Iterator<Player> it = this.openinventories.keySet().iterator();
        while (it.hasNext()) {
            it.next().closeInventory();
        }
        getLogger().log(Level.INFO, (prefix + ChatColor.RED + "편의기능이 비활성화되었습니다!"));
    }


    public static ShulkerPacks getInstance() {
        return plugin;
    }


    public boolean getPvpTimer(Player player) {
        if (pvp_timer.containsKey(player)) {
            return System.currentTimeMillis() - pvp_timer.get(player) < 7000;
        }
        return false;
    }

    public void setPvpTimer(Player player) {
        if (settings.pvp_timer_enabled) {
            pvp_timer.put(player, System.currentTimeMillis());
        }
    }
}
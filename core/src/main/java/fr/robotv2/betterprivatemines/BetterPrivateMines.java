package fr.robotv2.betterprivatemines;

import com.google.gson.Gson;
import fr.robotv2.api.grid.Grid;
import fr.robotv2.betterprivatemines.world.BukkitWorldManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public final class BetterPrivateMines extends JavaPlugin {

    private final static Gson gson = new Gson();

    private BukkitWorldManager bukkitWorldManager;

    private Grid grid;

    public static BetterPrivateMines instance() {
        return JavaPlugin.getPlugin(BetterPrivateMines.class);
    }

    public static Logger logger() {
        return instance().getLogger();
    }

    public static Gson gson() {
        return gson;
    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.bukkitWorldManager = new BukkitWorldManager(this);
    }

    @Override
    public void onDisable() {
    }

    public void onReload() {
        reloadConfig();
    }

    public void createGrid() {

    }
}
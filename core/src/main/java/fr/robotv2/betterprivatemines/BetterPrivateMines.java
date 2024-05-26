package fr.robotv2.betterprivatemines;

import com.google.gson.Gson;
import fr.robotv2.betterprivatemines.mine.PrivateMineManager;
import fr.robotv2.betterprivatemines.schematic.SchematicManager;
import fr.robotv2.betterprivatemines.world.WorldManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public final class BetterPrivateMines extends JavaPlugin {

    private final static Gson gson = new Gson();

    private PrivateMineManager privateMineManager;
    private WorldManager worldManager;
    private SchematicManager schematicManager;

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

        this.privateMineManager = new PrivateMineManager(this);
        this.worldManager = new WorldManager(this);
        this.schematicManager = new SchematicManager(this);

        getPrivateMineManager().loadConfigurations(getConfig().getConfigurationSection("mines"));
        getSchematicManager().loadSchematics();
    }

    @Override
    public void onDisable() {
        reloadConfig();
        getSchematicManager().saveSchematicPlacement();
    }

    public void onReload() {
        reloadConfig();
        getPrivateMineManager().loadConfigurations(getConfig().getConfigurationSection("mines"));
        getSchematicManager().loadSchematics();
    }
}
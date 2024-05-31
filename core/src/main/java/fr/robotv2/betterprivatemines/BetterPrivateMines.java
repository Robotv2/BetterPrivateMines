package fr.robotv2.betterprivatemines;

import fr.robotv2.api.grid.GridManager;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.mine.PrivateMineFactory;
import fr.robotv2.api.mine.PrivateMineManager;
import fr.robotv2.api.schematic.SchematicManager;
import fr.robotv2.betterprivatemines.command.BetterPrivateMinesCommand;
import fr.robotv2.betterprivatemines.config.BukkitPrivateMineConfiguration;
import fr.robotv2.betterprivatemines.config.BukkitPrivateMineConfigurationManager;
import fr.robotv2.betterprivatemines.listener.SystemListeners;
import fr.robotv2.betterprivatemines.world.BukkitWorldManager;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.util.stream.Collectors;

@Getter
public final class BetterPrivateMines extends JavaPlugin {

    private BukkitWorldManager bukkitWorldManager;

    private SchematicManager schematicManager;

    private GridManager gridManager;

    private PrivateMineManager privateMineManager;

    private PrivateMineFactory privateMineFactory;

    private BukkitPrivateMineConfigurationManager configurationManager;

    public static BetterPrivateMines instance() {
        return JavaPlugin.getPlugin(BetterPrivateMines.class);
    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.bukkitWorldManager = new BukkitWorldManager(this);
        this.schematicManager = new SchematicManager(new File(getDataFolder(), "schematics"));
        this.gridManager = new GridManager(new File(getDataFolder(), "world_state.json"));
        this.privateMineFactory = new PrivateMineFactory(privateMineManager, schematicManager, bukkitWorldManager, gridManager.getGrid());
        this.configurationManager = new BukkitPrivateMineConfigurationManager();

        getSchematicManager().loadSchematics();
        getConfigurationManager().loadConfigurations(getConfig().getConfigurationSection("mines"));

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getGridManager().saveGrid();
    }

    public void onReload() {
        reloadConfig();
        getSchematicManager().loadSchematics();
        getConfigurationManager().loadConfigurations(getConfig().getConfigurationSection("mines"));
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SystemListeners(this), this);
    }

    private void registerCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        handler.getAutoCompleter().registerSuggestion("mines", (args, sender, command) -> {
            return getConfigurationManager().getConfigurations().stream().map(PrivateMineConfiguration::getConfigurationName).collect(Collectors.toList());
        });

        handler.register(new BetterPrivateMinesCommand(this));
    }
}
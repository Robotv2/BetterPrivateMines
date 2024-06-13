package fr.robotv2.betterprivatemines;

import com.cryptomorin.xseries.XMaterial;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.MySqlConnection;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.robotv2.adapter.LatestWorldEditAdapter;
import fr.robotv2.adapter.LegacyWorldEditAdapter;
import fr.robotv2.api.BetterPrivateMinesPlugin;
import fr.robotv2.api.grid.GridManager;
import fr.robotv2.api.material.MineMaterialRegistry;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.mine.PrivateMineFactory;
import fr.robotv2.api.mine.PrivateMineManager;
import fr.robotv2.api.schematic.SchematicManager;
import fr.robotv2.api.storage.CompletableStorage;
import fr.robotv2.api.storage.StorageType;
import fr.robotv2.api.storage.impl.PerValueFileStorage;
import fr.robotv2.api.storage.impl.SqlStorage;
import fr.robotv2.api.worldedit.WorldEditAdapter;
import fr.robotv2.betterprivatemines.command.BetterPrivateMinesCommand;
import fr.robotv2.betterprivatemines.config.BukkitPrivateMineConfigurationManager;
import fr.robotv2.betterprivatemines.listener.MineEnterQuitListener;
import fr.robotv2.betterprivatemines.listener.SystemListeners;
import fr.robotv2.betterprivatemines.material.BukkitMineMaterial;
import fr.robotv2.betterprivatemines.material.OraxenMineMaterial;
import fr.robotv2.betterprivatemines.util.McVersion;
import fr.robotv2.betterprivatemines.world.BukkitWorldManager;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@Log
public final class BetterPrivateMines extends JavaPlugin implements BetterPrivateMinesPlugin {

    private BukkitWorldManager worldManager;

    private SchematicManager schematicManager;

    private GridManager gridManager;

    private PrivateMineManager privateMineManager;

    private CompletableStorage<UUID, PrivateMine> privateMineStorage;

    private BukkitPrivateMineConfigurationManager configurationManager;

    private PrivateMineFactory privateMineFactory;


    private File mineConfigurationFolder;

    private BukkitTask saveTask;


    public static BetterPrivateMines instance() {
        return JavaPlugin.getPlugin(BetterPrivateMines.class);
    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        BetterPrivateMinesPlugin.setInstance(this);
        saveDefaultConfig();
        setupStorageSystem();
        registerDefaultProviders();

        this.mineConfigurationFolder = new File(getDataFolder(), "mines");

        this.privateMineManager = new PrivateMineManager(this);
        this.worldManager = new BukkitWorldManager(this);
        this.schematicManager = new SchematicManager(new File(getDataFolder(), "schematics"));
        this.gridManager = new GridManager(new File(getDataFolder(), "world_state.json"));
        this.configurationManager = new BukkitPrivateMineConfigurationManager(this);

        this.privateMineFactory = new PrivateMineFactory(this);

        if(McVersion.current().isAtLeast(1, 16, 5)) {
            WorldEditAdapter.setWorldEditAdapter(new LatestWorldEditAdapter(mineMaterial -> mineMaterial instanceof BukkitMineMaterial ? ((BukkitMineMaterial) mineMaterial).getMaterial() : XMaterial.AIR));
        } else {
            WorldEditAdapter.setWorldEditAdapter(new LegacyWorldEditAdapter(mineMaterial -> mineMaterial instanceof BukkitMineMaterial ? ((BukkitMineMaterial) mineMaterial).getMaterial() : XMaterial.AIR));
        }

        getSchematicManager().loadSchematics();
        getConfigurationManager().loadConfigurations(getMineConfigurationFolder());

        registerListeners();
        registerCommands();

        startSaveAllTask();

        getLogger().info("Loading mines...");
        getPrivateMineStorage().selectAll().thenAccept((mines) -> {
            mines.forEach(getPrivateMineManager()::register);
            getLogger().info(String.format("All mines (%s) have been loaded successfully.", mines.size()));
        }).exceptionally(throwable -> {
            getLogger().log(Level.SEVERE, "An error occurred while loading mines.", throwable);
            return null;
        });
    }

    @Override
    public void onDisable() {

        if(saveTask != null) {
            saveTask.cancel();
            saveTask = null;
        }

        getGridManager().saveGrid();
        getPrivateMineManager().saveAll();
        getPrivateMineStorage().close();
    }

    public void onReload() {
        reloadConfig();
        getSchematicManager().loadSchematics();
        getConfigurationManager().loadConfigurations(getMineConfigurationFolder());
        startSaveAllTask();
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SystemListeners(this), this);
        pm.registerEvents(new MineEnterQuitListener(), this);
    }

    private void registerCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        handler.getAutoCompleter().registerSuggestion("mines", (args, sender, command) -> getConfigurationManager().getConfigurations().stream().map(PrivateMineConfiguration::getConfigurationName).collect(Collectors.toList()));
        handler.register(new BetterPrivateMinesCommand(this));
    }

    private void registerDefaultProviders() {
        MineMaterialRegistry.registerProvider("internal", BukkitMineMaterial::from);
        MineMaterialRegistry.registerProvider("oraxen", OraxenMineMaterial::from);
    }

    private void setupStorageSystem() {

        final StorageType type = StorageType.resolveType(getConfig().getString("storage.type"), StorageType.JSON);

        switch (type) {
            case JSON:
                this.privateMineStorage = CompletableStorage.wrap(new PerValueFileStorage<>(new File(getDataFolder(), "mines_data"), PrivateMine.class));
                return;
            case SQLITE:
                this.privateMineStorage = new SqlStorage(new SqliteConnection(DatabaseConfiguration.sqlite(getConfig().getBoolean("storage.debug")), getDataFolder()));
                return;
            case MYSQL:
                final DatabaseConfiguration configuration = DatabaseConfiguration.create(
                        getConfig().getString("storage.configuration.user"),
                        getConfig().getString("storage.configuration.password"),
                        getConfig().getInt("storage.configuration.port"),
                        getConfig().getString("storage.configuration.address"),
                        getConfig().getString("storage.configuration.database"),
                        getConfig().getBoolean("storage.debug"),
                        DatabaseType.MYSQL
                );
                this.privateMineStorage = new SqlStorage(new MySqlConnection(configuration));
        }
    }

    private void startSaveAllTask() {

        if(saveTask != null) {
            saveTask.cancel();
        }

        final int delay = getConfig().getInt("save_all_delay", 30) * 20;
        saveTask = getServer().getScheduler().runTaskTimer(this, () -> getPrivateMineManager().saveAll(), delay, delay);
    }
}
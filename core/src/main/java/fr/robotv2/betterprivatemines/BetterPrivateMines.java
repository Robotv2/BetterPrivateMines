package fr.robotv2.betterprivatemines;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.adapter.LatestWorldEditAdapter;
import fr.robotv2.adapter.LegacyWorldEditAdapter;
import fr.robotv2.api.BetterPrivateMinesPlugin;
import fr.robotv2.api.grid.GridManager;
import fr.robotv2.api.mine.*;
import fr.robotv2.api.schematic.SchematicManager;
import fr.robotv2.api.storage.CompletableStorage;
import fr.robotv2.api.storage.impl.PerValueFileStorage;
import fr.robotv2.api.worldedit.WorldEditAdapter;
import fr.robotv2.betterprivatemines.command.BetterPrivateMinesCommand;
import fr.robotv2.betterprivatemines.config.BukkitPrivateMineConfigurationManager;
import fr.robotv2.betterprivatemines.listener.MineEnterQuitListener;
import fr.robotv2.betterprivatemines.listener.SystemListeners;
import fr.robotv2.betterprivatemines.material.BukkitMineMaterial;
import fr.robotv2.betterprivatemines.util.McVersion;
import fr.robotv2.betterprivatemines.world.BukkitWorldManager;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class BetterPrivateMines extends JavaPlugin implements BetterPrivateMinesPlugin {

    private BukkitWorldManager worldManager;

    private SchematicManager schematicManager;

    private GridManager gridManager;

    private PrivateMineManager privateMineManager;

    private CompletableStorage<UUID, PrivateMine> privateMineStorage;

    private BukkitPrivateMineConfigurationManager privateMineConfigurationManager;

    private PrivateMineFactory privateMineFactory;

    public static BetterPrivateMines instance() {
        return JavaPlugin.getPlugin(BetterPrivateMines.class);
    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();

        this.privateMineStorage = CompletableStorage.wrap(new PerValueFileStorage<>(new File(getDataFolder(), "mines"), PrivateMine.class));
        this.privateMineManager = new PrivateMineManager();
        this.worldManager = new BukkitWorldManager(this);
        this.schematicManager = new SchematicManager(new File(getDataFolder(), "schematics"));
        this.gridManager = new GridManager(new File(getDataFolder(), "world_state.json"));
        this.privateMineConfigurationManager = new BukkitPrivateMineConfigurationManager();

        this.privateMineFactory = new PrivateMineFactory(this);

        if(McVersion.current().isAtLeast(1, 16, 5)) {
            WorldEditAdapter.setWorldEditAdapter(new LatestWorldEditAdapter(mineMaterial -> mineMaterial instanceof BukkitMineMaterial ? ((BukkitMineMaterial) mineMaterial).getMaterial() : XMaterial.AIR));
        } else {
            WorldEditAdapter.setWorldEditAdapter(new LegacyWorldEditAdapter(mineMaterial -> mineMaterial instanceof BukkitMineMaterial ? ((BukkitMineMaterial) mineMaterial).getMaterial() : XMaterial.AIR));
        }

        getSchematicManager().loadSchematics();
        getPrivateMineConfigurationManager().loadConfigurations(getConfig().getConfigurationSection("mines"));

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
        getPrivateMineConfigurationManager().loadConfigurations(getConfig().getConfigurationSection("mines"));
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SystemListeners(this), this);
        pm.registerEvents(new MineEnterQuitListener(), this);
    }

    private void registerCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        handler.getAutoCompleter().registerSuggestion("mines", (args, sender, command) -> getPrivateMineConfigurationManager().getConfigurations().stream().map(PrivateMineConfiguration::getConfigurationName).collect(Collectors.toList()));
        handler.register(new BetterPrivateMinesCommand(this));
    }
}
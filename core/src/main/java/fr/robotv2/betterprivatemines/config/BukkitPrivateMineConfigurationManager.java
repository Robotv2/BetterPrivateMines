package fr.robotv2.betterprivatemines.config;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.mine.PrivateMineConfigurationManager;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BukkitPrivateMineConfigurationManager implements PrivateMineConfigurationManager<XMaterial> {

    private final BetterPrivateMines plugin;
    private final Map<String, BukkitPrivateMineConfiguration> configurations = new HashMap<>();

    public BukkitPrivateMineConfigurationManager(BetterPrivateMines plugin) {
        this.plugin = plugin;
    }

    public void loadConfigurations(final File folder) {

        configurations.clear();

        if(!folder.exists()) {
            folder.mkdir();
            setupDefaultMines(folder);
        }

        final File[] files = folder.listFiles();

        if(files == null) {
            return;
        }

        for(File file : files) {
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            final BukkitPrivateMineConfiguration bukkitPrivateMineConfiguration = new BukkitPrivateMineConfiguration(file, configuration);
            configurations.put(FileUtil.getNameWithoutExtension(file), bukkitPrivateMineConfiguration);
        }
    }

    @Override
    @Nullable
    @Contract("null -> null")
    public BukkitPrivateMineConfiguration getConfiguration(String configName) {
        if(configName == null) return null;
        return configurations.get(configName.toLowerCase());
    }

    @Override
    @UnmodifiableView
    public Collection<PrivateMineConfiguration<XMaterial>> getConfigurations() {
        return Collections.unmodifiableCollection(configurations.values());
    }

    private void setupDefaultMines(final File folder) {
        plugin.saveResource(folder.getName() + File.separator + "mine1.yml", false); // TODO
    }
}

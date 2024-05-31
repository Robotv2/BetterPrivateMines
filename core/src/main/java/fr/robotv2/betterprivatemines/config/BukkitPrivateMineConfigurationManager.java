package fr.robotv2.betterprivatemines.config;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.mine.PrivateMineConfigurationManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BukkitPrivateMineConfigurationManager implements PrivateMineConfigurationManager<XMaterial> {

    private final Map<String, BukkitPrivateMineConfiguration> configurations = new HashMap<>();

    public void loadConfigurations(final ConfigurationSection section) {

        configurations.clear();
        if(section == null) return;

        for(String key : section.getKeys(false)) {
            final BukkitPrivateMineConfiguration configuration = new BukkitPrivateMineConfiguration(section.getConfigurationSection(key));
            configurations.put(key.toLowerCase(), configuration);
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
}

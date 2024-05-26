package fr.robotv2.betterprivatemines.mine;

import fr.robotv2.betterprivatemines.BetterPrivateMines;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class PrivateMineManager {

    private final BetterPrivateMines plugin;
    private final Map<String, PrivateMineConfiguration> configurations = new HashMap<>();

    public void loadConfigurations(final ConfigurationSection section) {

        configurations.clear();
        if(section == null) return;

        section.getKeys(false).forEach(key -> {
            configurations.put(key.toLowerCase(Locale.ENGLISH), new PrivateMineConfiguration(section.getConfigurationSection(key)));
        });
    }

    @Nullable
    public PrivateMineConfiguration getConfiguration(final String configurationName) {
        if(configurationName == null) return null;
        return configurations.get(configurationName.toLowerCase(Locale.ENGLISH));
    }
}

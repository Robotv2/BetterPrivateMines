package fr.robotv2.betterprivatemines.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.robotv2.api.material.MineBlockSet;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.material.MineMaterialRegistry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ConfigurationUtil {

    public static <K, V> Map<K, V> loadMap(final ConfigurationSection section, Function<String, K> keyMapper, Function<String, V> valueMapper, V defaultValue) {
        if (section == null) return Collections.emptyMap();
        final Map<K, V> values = new HashMap<>();

        section.getKeys(false).forEach(key -> {
            K mappedKey = keyMapper.apply(key);
            V mappedValue = valueMapper.apply(section.getString(key, String.valueOf(defaultValue)));
            values.put(mappedKey, mappedValue);
        });

        return values;
    }

    public static <K, V> BiMap<K, V> loadBiMap(final ConfigurationSection section, Function<String, K> keyMapper, Function<String, V> valueMapper) {
        if (section == null) return HashBiMap.create(0);
        final BiMap<K, V> biMap = HashBiMap.create(section.getKeys(false).size());

        section.getKeys(false).forEach(key -> {
            K mappedKey = keyMapper.apply(key);
            V mappedValue = valueMapper.apply(section.getString(key));
            if (mappedKey != null && mappedValue != null) {
                biMap.put(mappedKey, mappedValue);
            }
        });

        return biMap;
    }

    public static Map<Integer, MineBlockSet> loadMineBlockSets(final ConfigurationSection section) {
        if (section == null) return Collections.emptyMap();
        final Map<Integer, MineBlockSet> levelSets = new HashMap<>();

        section.getKeys(false).forEach(key -> {

            Integer level = Integer.valueOf(key);
            ConfigurationSection innerSection = section.getConfigurationSection(key);

            if (innerSection != null) {

                MineBlockSet mineBlockSet = new MineBlockSet();

                innerSection.getKeys(false).forEach(materialKey -> {

                    MineMaterial material = MineMaterialRegistry.resolve(materialKey);
                    double chance = innerSection.getDouble(materialKey);
                    mineBlockSet.addChance(material, chance);

                });

                levelSets.put(level, mineBlockSet);
            }
        });

        return levelSets;
    }
}

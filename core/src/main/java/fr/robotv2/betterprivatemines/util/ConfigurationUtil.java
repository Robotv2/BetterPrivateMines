package fr.robotv2.betterprivatemines.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
}

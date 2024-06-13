package fr.robotv2.api.material;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class MineBlockSet {

    private final Map<MineMaterial, Double> delegate;

    public MineBlockSet() {
        this.delegate = new HashMap<>();
    }

    public void addChance(MineMaterial mineMaterial, double chance) {
        delegate.put(mineMaterial, chance);
    }

    public MineMaterial getRandomMaterial() {
        return getRandomMaterial(delegate.entrySet());
    }

    public <T extends MineMaterial> T getRandomMaterial(Class<T> clazz) {
        Map<T, Double> materials = delegate.entrySet().stream()
                .filter(entry -> clazz.isAssignableFrom(entry.getKey().getClass()))
                .collect(Collectors.toMap(
                        entry -> clazz.cast(entry.getKey()),
                        Map.Entry::getValue
                ));
        return getRandomMaterial(materials.entrySet());
    }

    private <T extends MineMaterial> T getRandomMaterial(Set<Map.Entry<T, Double>> entries) {

        double total = entries.stream().mapToDouble(Map.Entry::getValue).sum();
        double random = ThreadLocalRandom.current().nextDouble(total);

        for (Map.Entry<T, Double> entry : entries) {
            random -= entry.getValue();
            if (random <= 0) {
                return entry.getKey();
            }
        }

        return null;
    }
}


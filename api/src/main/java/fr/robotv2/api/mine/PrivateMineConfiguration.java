package fr.robotv2.api.mine;

import com.google.common.collect.BiMap;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.position.MinePositionProcessorConfig;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.ResetType;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
public abstract class PrivateMineConfiguration<T> {

    protected final String configurationName;

    protected final String schematicName;

    protected final int initialSize;

    protected final BiMap<MinePositionType, T> positions;

    protected final ResetType resetType;

    protected  final int maxBlockPerTick;

    protected final Map<MineMaterial, Double> materials;

    public MineMaterial getRandomMaterial() {
        return getRandomMaterial(getMaterials().entrySet());
    }

    public MineMaterial getRandomMaterial(Class<? extends MineMaterial> clazz) {
        return getRandomMaterial(getMaterials().entrySet().stream().filter(entry -> clazz.isAssignableFrom(entry.getKey().getClass())).collect(Collectors.toSet()));
    }

    private MineMaterial getRandomMaterial(Set<Map.Entry<MineMaterial, Double>> entries) {

        final double total = entries.stream().mapToDouble(Map.Entry::getValue).sum();
        double random = ThreadLocalRandom.current().nextDouble(total);

        for (Map.Entry<MineMaterial, Double> entry : entries) {
            random -= entry.getValue();
            if (random <= 0) {
                return entry.getKey();
            }
        }

        return null;
    }

    public abstract MinePositionProcessorConfig<T> getMineProcessorConfig();
}

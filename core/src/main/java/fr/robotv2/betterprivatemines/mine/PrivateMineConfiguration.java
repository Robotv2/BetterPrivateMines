package fr.robotv2.betterprivatemines.mine;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.mine.material.InternalMineMaterial;
import fr.robotv2.betterprivatemines.mine.material.MineMaterial;
import fr.robotv2.betterprivatemines.mine.position.MinePositionType;
import fr.robotv2.betterprivatemines.reset.ResetType;
import fr.robotv2.betterprivatemines.schematic.MineSchematic;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class PrivateMineConfiguration {

    private final String configurationName;

    private final ResetType resetType;

    private final int maxBlockPerTick;

    private final Map<MineMaterial, Double> materials;

    private final BiMap<MinePositionType, XMaterial> positions;

    private final int initialSize;

    private final String schematicName;

    public PrivateMineConfiguration(final ConfigurationSection section) {
        this.configurationName = section.getName();
        this.maxBlockPerTick = section.getInt("reset.max_block_per_tick", 20);
        this.resetType = ResetType.resolveType(section.getString("reset.type"), ResetType.FULL);
        this.materials = loadMaterials(section.getConfigurationSection("materials"));
        this.positions = loadPositions(section.getConfigurationSection("positions"));
        this.initialSize = section.getInt("initial_size", 3);
        this.schematicName = section.getString("schematic");
    }

    public MineMaterial getRandomMaterial() {

        final double total = materials.values().stream().mapToDouble(Double::doubleValue).sum();
        double random = ThreadLocalRandom.current().nextDouble(total);

        for (Map.Entry<MineMaterial, Double> entry : materials.entrySet()) {
            random -= entry.getValue();
            if (random <= 0) {
                return entry.getKey();
            }
        }

        return InternalMineMaterial.INTERNAL_AIR;
    }

    @Nullable
    public MineSchematic getMineSchematic() {
        return BetterPrivateMines.instance().getSchematicManager().getMineSchematic(getSchematicName());
    }

    private Map<MineMaterial, Double> loadMaterials(final ConfigurationSection materialSection) {

        if(materialSection == null) return Collections.emptyMap();
        final Map<MineMaterial, Double> materials = new HashMap<>();

        for(String key : materialSection.getKeys(false)) {
            final MineMaterial mineMaterial = MineMaterial.resolve(key);
            if(mineMaterial == null) continue;
            materials.put(mineMaterial, materialSection.getDouble(key, 0));
        }

        return materials;
    }

    private BiMap<MinePositionType, XMaterial> loadPositions(final ConfigurationSection positionSection) {

        if(positionSection == null) {
            return HashBiMap.create(0);
        }

        final BiMap<MinePositionType, XMaterial> positions = HashBiMap.create(MinePositionType.values().length);

        for(MinePositionType type : MinePositionType.VALUES) {
            final String materialLiteral = positionSection.getString(type.lower());
            if(materialLiteral == null) continue;
            final XMaterial xMaterial = XMaterial.matchXMaterial(materialLiteral).orElseThrow(() -> new NullPointerException(materialLiteral + " is not a valid material type."));
            positions.put(type, xMaterial);
        }

        return positions;
    }
}

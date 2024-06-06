package fr.robotv2.betterprivatemines.config;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.position.MinePositionProcessorConfig;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.ResetType;
import fr.robotv2.betterprivatemines.material.BukkitMineMaterial;
import fr.robotv2.betterprivatemines.util.ConfigurationUtil;
import fr.robotv2.betterprivatemines.util.MaterialUtil;
import fr.robotv2.betterprivatemines.util.PositionAdapter;
import org.bukkit.configuration.ConfigurationSection;

public class BukkitPrivateMineConfiguration extends PrivateMineConfiguration<XMaterial> {

    public BukkitPrivateMineConfiguration(final ConfigurationSection section) {
        super(
                section.getName(),
                section.getString("schematic"),
                section.getInt("initial_size"),
                ConfigurationUtil.loadBiMap(
                        section.getConfigurationSection("positions"),
                        literal -> MinePositionType.valueOf(literal.toUpperCase()),
                        MaterialUtil::matchXMaterialOrThrow
                ),
                ResetType.resolveType(section.getString("reset.type"), ResetType.FULL),
                section.getInt("reset.max_block_per_tick"),
                ConfigurationUtil.loadMap(
                        section.getConfigurationSection("materials"),
                        BukkitMineMaterial::from,
                        Double::parseDouble,
                        0D
                )
        );
    }

    @Override
    public MinePositionProcessorConfig<XMaterial> getMineProcessorConfig() {
        return new MinePositionProcessorConfig<>(
                getPositions().inverse(),
                position -> XMaterial.matchXMaterial(PositionAdapter.fromPosition(position).getBlock().getType())
        );
    }
}

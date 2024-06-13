package fr.robotv2.betterprivatemines.config;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.position.MinePositionProcessorConfig;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.ResetType;
import fr.robotv2.betterprivatemines.util.ConfigurationUtil;
import fr.robotv2.betterprivatemines.util.FileUtil;
import fr.robotv2.betterprivatemines.util.MaterialUtil;
import fr.robotv2.betterprivatemines.util.PositionAdapter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class BukkitPrivateMineConfiguration extends PrivateMineConfiguration<XMaterial> {

    public BukkitPrivateMineConfiguration(final File file, final ConfigurationSection section) {
        super(
                FileUtil.getNameWithoutExtension(file),
                section.getString("schematic"),
                section.getInt("initial_size"),
                ConfigurationUtil.loadBiMap(
                        section.getConfigurationSection("positions"),
                        literal -> MinePositionType.valueOf(literal.toUpperCase()),
                        MaterialUtil::matchXMaterialOrThrow
                ),
                ResetType.resolveType(section.getString("reset.type"), ResetType.FULL),
                section.getInt("reset.max_block_per_tick"),
                ConfigurationUtil.loadMineBlockSets(section.getConfigurationSection("materials"))
        );
    }

    @Override
    public MinePositionProcessorConfig<XMaterial> getMineProcessorConfig() {
        return new MinePositionProcessorConfig<>(
                getPositions().inverse(),
                position -> XMaterial.matchXMaterial(PositionAdapter.fromPosition(position).getBlock().getType()),
                position -> PositionAdapter.fromPosition(position).getBlock().setType(Material.AIR)
        );
    }
}

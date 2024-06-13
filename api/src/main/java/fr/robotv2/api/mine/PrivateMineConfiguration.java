package fr.robotv2.api.mine;

import com.google.common.collect.BiMap;
import fr.robotv2.api.material.MineBlockSet;
import fr.robotv2.api.position.MinePositionProcessorConfig;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.ResetType;
import lombok.Data;

import java.util.Map;

@Data
public abstract class PrivateMineConfiguration<T> {

    protected final String configurationName;

    protected final String schematicName;

    protected final int initialSize;

    protected final BiMap<MinePositionType, T> positions;

    protected final ResetType resetType;

    protected  final int maxBlockPerTick;

    protected final Map<Integer, MineBlockSet> levelSets;

    public abstract MinePositionProcessorConfig<T> getMineProcessorConfig();

    public MineBlockSet getLevelSet(int level) {
        while (level > 0) {

            MineBlockSet levelSet = levelSets.get(level);

            if (levelSet != null) {
                return levelSet;
            }

            level--;
        }

        return null;
    }
}

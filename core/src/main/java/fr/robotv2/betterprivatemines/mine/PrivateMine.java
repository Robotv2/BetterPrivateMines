package fr.robotv2.betterprivatemines.mine;

import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.mine.position.MinePosition;
import fr.robotv2.betterprivatemines.mine.position.MinePositionType;
import fr.robotv2.betterprivatemines.reset.MineResetInterface;
import fr.robotv2.betterprivatemines.reset.ResetType;
import fr.robotv2.betterprivatemines.reset.impl.FullReset;
import fr.robotv2.betterprivatemines.vector.BoundingBox;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Data
public final class PrivateMine {

    private final UUID mineId;
    private final UUID owner;
    private int size;

    private final BoundingBox entireZoneBox;
    private final BoundingBox mineAreaBox;

    private transient BoundingBox mineableAreaBox;

    private final String worldName;
    private final String configurationName;

    public PrivateMine(UUID ownerId, PrivateMineConfiguration configuration, MinePosition minePosition, BoundingBox boundingBox) {
        this.mineId = UUID.randomUUID();
        this.owner = ownerId;
        this.size = configuration.getInitialSize();
        this.entireZoneBox = boundingBox;
        this.mineAreaBox = minePosition.toBoundingBox(MinePositionType.MINE_CORNER);
        this.worldName = boundingBox.getWorldName();
        this.configurationName = configuration.getConfigurationName();
    }

    @NotNull
    public PrivateMineConfiguration getConfiguration() {
        return Objects.requireNonNull(BetterPrivateMines.instance().getPrivateMineManager().getConfiguration(configurationName), "private mine configuration");
    }

    public BoundingBox getMineableAreaBox() {
        return mineableAreaBox == null ? (mineableAreaBox = getMineAreaBox().innerBoundingBox(size)) : mineableAreaBox;
    }

    public void reset(final MineResetInterface type) {
        type.reset(this);
    }

    public void reset() {
        reset(getConfiguration().getResetType());
    }

    public void expand(int size) {
        this.mineableAreaBox = null;
        this.size += size;
    }

    public void expand() {
        expand(1);
    }
}

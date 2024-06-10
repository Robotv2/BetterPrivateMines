package fr.robotv2.api.mine;

import fr.robotv2.api.position.MinePosition;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.MineResetInterface;
import fr.robotv2.api.storage.Identifiable;
import fr.robotv2.api.vector.BoundingBox;
import lombok.Data;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Data
public class PrivateMine implements Identifiable<UUID> {

    private final UUID mineId;

    private final UUID ownerId;

    private final String configurationName;

    private final PrivateMineConfigurationManager<?> confManager;

    private final MinePosition minePosition;

    private final BoundingBox entireZoneBox;

    private final BoundingBox mineAreaBox;

    private BoundingBox mineableAreaBox = null;

    private String mineName;

    private int size = 2;

    public PrivateMine(UUID mineId, UUID ownerId, String configurationName, PrivateMineConfigurationManager<?> confManager, MinePosition minePosition, BoundingBox entireZoneBox, BoundingBox mineAreaBox) {
        this.mineId = mineId;
        this.ownerId = ownerId;
        this.configurationName = configurationName;
        this.confManager = confManager;
        this.minePosition = minePosition;
        this.entireZoneBox = entireZoneBox;
        this.mineAreaBox = mineAreaBox;
    }

    public PrivateMine(UUID ownerId, String configurationName, PrivateMineConfigurationManager<?> confManager, MinePosition minePosition, BoundingBox entireZoneBox) {
        this.mineId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.configurationName = configurationName;
        this.confManager = confManager;
        this.minePosition = minePosition;
        this.entireZoneBox = entireZoneBox;
        this.mineAreaBox = minePosition.toBoundingBox(MinePositionType.MINE_CORNER);
    }

    @Override
    public UUID getId() {
        return mineId;
    }

    public PrivateMineConfiguration<?> getConfiguration() {
        return confManager.getConfiguration(getConfigurationName());
    }

    public BoundingBox getMineableAreaBox() {
        return (mineableAreaBox != null) ? mineableAreaBox : (getMineAreaBox().innerBoundingBox(size));
    }

    public void expand(int size) {
        this.mineableAreaBox = null;
        this.size += size;
    }

    public void expand() {
        expand(1);
    }

    public void reset(MineResetInterface mineResetInterface) {
        mineResetInterface.reset(this);
    }

    public void reset() {
        reset(getConfiguration().getResetType());
    }

    @Override
    public boolean equals(Object object) {

        if(object == this) {
            return true;
        }

        if(!(object instanceof PrivateMine)) {
            return false;
        }

        return Objects.equals(((PrivateMine) object).getMineId(), getMineId());
    }
}

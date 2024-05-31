package fr.robotv2.api.mine;

import fr.robotv2.api.position.MinePosition;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.MineResetInterface;
import fr.robotv2.api.vector.BoundingBox;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;

@Data
public class PrivateMine {

    private final UUID mineId;

    private final UUID ownerId;

    private final PrivateMineConfiguration<?> configuration;

    private final MinePosition minePosition;

    private final BoundingBox entireZoneBox;

    private final BoundingBox mineAreaBox;

    private BoundingBox mineableAreaBox = null;

    private String mineName;

    private int size = 2;

    public PrivateMine(UUID mineId, UUID ownerId, PrivateMineConfiguration<?> configuration, MinePosition minePosition, BoundingBox entireZoneBox, BoundingBox mineAreaBox) {
        this.mineId = mineId;
        this.ownerId = ownerId;
        this.configuration = configuration;
        this.minePosition = minePosition;
        this.entireZoneBox = entireZoneBox;
        this.mineAreaBox = mineAreaBox;
    }

    public PrivateMine(UUID ownerId, PrivateMineConfiguration<?> configuration, MinePosition minePosition, BoundingBox entireZoneBox) {
        this.mineId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.configuration = configuration;
        this.minePosition = minePosition;
        this.entireZoneBox = entireZoneBox;
        this.mineAreaBox = minePosition.toBoundingBox(MinePositionType.MINE_CORNER);
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

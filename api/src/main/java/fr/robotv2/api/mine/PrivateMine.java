package fr.robotv2.api.mine;

import fr.robotv2.api.BetterPrivateMinesPlugin;
import fr.robotv2.api.json.post.PostProcessable;
import fr.robotv2.api.position.MinePosition;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.reset.MineResetInterface;
import fr.robotv2.api.storage.Identifiable;
import fr.robotv2.api.storage.dto.PrivateMineDto;
import fr.robotv2.api.vector.BoundingBox;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@ToString
public class PrivateMine implements Identifiable<UUID>, PostProcessable {

    // <-- FINAL FIELDS -->

    private final UUID mineId;

    private final UUID ownerId;

    private final String configurationName;

    private final MinePosition minePosition;

    private final BoundingBox entireZoneBox;

    private final BoundingBox mineAreaBox;

    private final Set<UUID> allowed;

    // <-- MODIFIABLE FIELDS -->

    private PrivateMineState state;

    private int size;

    private int level;

    private String mineName = null;

    // <-- INTERNAL LOGIC FIELDS -->

    private transient BoundingBox mineableAreaBox;

    private transient AtomicBoolean isDirty;

    private transient AtomicBoolean isBeingReset;

    public PrivateMine(UUID ownerId, String configurationName, MinePosition minePosition, BoundingBox entireZoneBox) {
        this.mineId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.configurationName = configurationName;
        this.minePosition = minePosition;
        this.entireZoneBox = entireZoneBox;
        this.mineAreaBox = minePosition.toBoundingBox(MinePositionType.MINE_CORNER);
        this.allowed = new HashSet<>();

        this.state = PrivateMineState.PRIVATE;
        this.size = getConfiguration().getInitialSize();
        this.level = 1;
        this.mineName = null;

        postProcess();
    }

    @ApiStatus.Internal
    public PrivateMine(final PrivateMineDto dto) {
        this.mineId = dto.getMinedId();
        this.ownerId = dto.getOwnerId();
        this.configurationName = dto.getConfigurationName();
        this.minePosition = dto.getMinePosition();
        this.entireZoneBox = dto.getEntireZoneBox();
        this.mineAreaBox = dto.getMineAreaBox();
        this.allowed = dto.getAllowed();

        this.state = dto.getState();
        this.size = dto.getSize();
        this.level = dto.getLevel();
        this.mineName = dto.getMineName();

        postProcess();
    }

    @Override
    public void postProcess() {
        this.mineableAreaBox = null;
        this.isDirty = new AtomicBoolean(false);
        this.isBeingReset = new AtomicBoolean(false);
    }

    @Override
    public UUID getId() {
        return mineId;
    }

    @NotNull
    public String getMineName() {
        return mineName == null || mineName.isEmpty() ? getId().toString() : mineName;
    }

    public PrivateMineConfiguration<?> getConfiguration() {
        return BetterPrivateMinesPlugin.getInstance().getConfigurationManager().getConfiguration(getConfigurationName());
    }

    public BoundingBox getMineableAreaBox() {
        return (mineableAreaBox != null) ? mineableAreaBox : (getMineAreaBox().innerBoundingBox(size));
    }

    public void expand(int size) {
        this.mineableAreaBox = null;
        this.size += size;
        setNeedSaving(true);
    }

    public void expand() {
        expand(1);
    }

    public void reset(MineResetInterface mineResetInterface) {
        if (isBeingReset.compareAndSet(false, true)) {
            mineResetInterface.reset(this).whenComplete((ignored0, ignored1) -> isBeingReset.set(false));
        }
    }

    public void reset() {
        reset(getConfiguration().getResetType());
    }

    public void setAllowed(UUID playerId, boolean value) {

        if(Objects.equals(playerId, ownerId)) {
            return;
        }

        if(value) {
            allowed.add(playerId);
        } else {
            allowed.remove(playerId);
        }

        setNeedSaving(true);
    }

    public boolean isAllowed(UUID playerId) {
        return Objects.equals(playerId, ownerId) || allowed.contains(playerId);
    }

    public void setState(PrivateMineState state) {
        this.state = state;
        setNeedSaving(true);
    }

    public void setLevel(int level) {
        this.level = level;
        setNeedSaving(true);
    }

    public void setMineName(String mineName) {
        this.mineName = mineName;
        setNeedSaving(true);
    }

    public boolean needSaving() {
        return isDirty.get();
    }

    public void setNeedSaving(boolean needSaving) {
        this.isDirty.set(needSaving);
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

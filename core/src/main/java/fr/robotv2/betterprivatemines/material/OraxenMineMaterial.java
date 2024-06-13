package fr.robotv2.betterprivatemines.material;

import com.google.common.base.Preconditions;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.vector.Position;
import fr.robotv2.betterprivatemines.util.PositionAdapter;
import io.th0rgal.oraxen.api.OraxenBlocks;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class OraxenMineMaterial implements MineMaterial {

    private final String itemId;

    private OraxenMineMaterial(String itemId) {
        Preconditions.checkArgument(OraxenBlocks.isOraxenBlock(itemId), itemId + " is not a valid oraxen block.");
        this.itemId = itemId;
    }

    public static OraxenMineMaterial from(final String itemId) {
        return new OraxenMineMaterial(itemId);
    }

    @Override
    public void place(Position location) {
        OraxenBlocks.place(itemId, PositionAdapter.fromPosition(location));
    }
}

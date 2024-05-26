package fr.robotv2.betterprivatemines.schematic.placement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
public final class SchematicPlacement {

    private AtomicReference<GridPosition> lastPosition;

    private final PositionResolver positionResolver;

    public SchematicPlacement(GridPosition initial, PositionResolver resolver) {
        this.lastPosition = new AtomicReference<>(initial);
        this.positionResolver = resolver;
    }

    public GridPosition next() {
        final GridPosition next = Objects.requireNonNull(positionResolver).next(getLastPosition().get());
        getLastPosition().set(next);
        return next;
    }
}

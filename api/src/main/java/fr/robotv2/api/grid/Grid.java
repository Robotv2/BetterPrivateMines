package fr.robotv2.api.grid;

import fr.robotv2.api.grid.impl.HelixGridPositionResolver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Grid implements java.io.Serializable {

    @Getter
    private GridPosition lastPosition;

    @Getter
    private final GridPositionResolver positionResolver;

    private final ReentrantLock lock = new ReentrantLock();

    public Grid(GridPosition initial, GridPositionResolver resolver) {
        this.lastPosition = initial;
        this.positionResolver = resolver;
    }

    public static Grid createDefault() {
        return new Grid(new GridPosition(0, 0), new HelixGridPositionResolver());
    }

    @NotNull
    public GridPosition next() {

        lock.lock();

        try {
            final GridPosition next = Objects.requireNonNull(positionResolver.next(getLastPosition()));
            lastPosition = next;
            return next;
        } finally {
            lock.unlock();
        }
    }
}

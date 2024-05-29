package fr.robotv2.api.vector;

import fr.robotv2.api.bucket.Bucket;
import fr.robotv2.api.bucket.BucketPartition;
import fr.robotv2.api.bucket.factory.BucketFactory;
import fr.robotv2.api.bucket.partitioning.PartitioningStrategies;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Consumer;

@Getter
public class BoundingBox implements java.io.Serializable {

    private final Position firstCorner;

    private final Position secondCorner;

    // pre-calculation

    protected final transient String worldName;

    protected final transient double minX;
    protected final transient double maxX;
    protected final transient double minY;
    protected final transient double maxY;
    protected final transient double minZ;
    protected final transient double maxZ;

    public BoundingBox(Position firstCorner, Position secondCorner) {
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;

        if(!Objects.equals(firstCorner.getWorldName(), secondCorner.getWorldName())) {
            throw new IllegalArgumentException("Both corners must be in the same world");
        }

        this.worldName = firstCorner.getWorldName();
        this.minX = Math.min(firstCorner.getX(), secondCorner.getX());
        this.maxX = Math.max(firstCorner.getX(), secondCorner.getX());
        this.minY = Math.min(firstCorner.getY(), secondCorner.getY());
        this.maxY = Math.max(firstCorner.getY(), secondCorner.getY());
        this.minZ = Math.min(firstCorner.getZ(), secondCorner.getZ());
        this.maxZ = Math.max(firstCorner.getZ(), secondCorner.getZ());
    }

    public void forEach(Consumer<Position> consumer) {
        for (int x = (int) minX; x <= maxX; x++) {
            for (int y = (int) minY; y <= maxY; y++) {
                for (int z = (int) minZ; z <= maxZ; z++) {
                    consumer.accept(Position.of(worldName, x, y, z));
                }
            }
        }
    }

    public Bucket<Position> asBucket(int maxBlockPerTick) {
        final Bucket<Position> bucket = BucketFactory.newConcurrentBucket(maxBlockPerTick, PartitioningStrategies.nextInCycle());
        forEach(bucket::add);
        return bucket;
    }

    public BoundingBox innerBoundingBox(double diagonalSize) {

        final Position center = center();

        // Calculate half-length of the diagonal in 3D space
        final double halfDiagonal = diagonalSize / 2.0;
        final double halfSideLength = halfDiagonal / Math.sqrt(3);

        final Position newFirstCorner = Position.of(
                worldName,
                center.getX() - halfSideLength,
                center.getY() - halfSideLength,
                center.getZ() - halfSideLength
        );

        final Position newSecondCorner = Position.of(
                worldName,
                center.getX() + halfSideLength,
                center.getY() + halfSideLength,
                center.getZ() + halfSideLength
        );

        return new BoundingBox(newFirstCorner, newSecondCorner);
    }

    public Position center() {
        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double centerZ = (minZ + maxZ) / 2.0;
        return Position.of(worldName, centerX, centerY, centerZ);
    }

    public boolean contains(final Position vector) {
        final double x = vector.getX();
        final double y = vector.getY();
        final double z = vector.getZ();
        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }
}

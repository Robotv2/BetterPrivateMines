package fr.robotv2.betterprivatemines.vector;

import fr.robotv2.betterprivatemines.bucket.Bucket;
import fr.robotv2.betterprivatemines.bucket.factory.BucketFactory;
import fr.robotv2.betterprivatemines.bucket.partitioning.PartitioningStrategies;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@Data
public class BoundingBox implements java.io.Serializable {

    private final Location firstCorner;

    private final Location secondCorner;

    protected final transient String worldName;

    // pre-calculation
    protected final transient double minX;
    protected final transient double maxX;
    protected final transient double minY;
    protected final transient double maxY;
    protected final transient double minZ;
    protected final transient double maxZ;

    public BoundingBox(final Location firstCorner, final Location secondCorner) {
        Objects.requireNonNull(firstCorner, "first corner can't be null");
        Objects.requireNonNull(secondCorner, "second corner can't be null");

        if(!firstCorner.getWorld().getUID().equals(secondCorner.getWorld().getUID())) {
            throw new IllegalArgumentException("Both corners must be in the same world.");
        }

        this.worldName = firstCorner.getWorld().getName();
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;

        this.minX = Math.min(firstCorner.getX(), secondCorner.getX());
        this.maxX = Math.max(firstCorner.getX(), secondCorner.getX());
        this.minY = Math.min(firstCorner.getY(), secondCorner.getY());
        this.maxY = Math.max(firstCorner.getY(), secondCorner.getY());
        this.minZ = Math.min(firstCorner.getZ(), secondCorner.getZ());
        this.maxZ = Math.max(firstCorner.getZ(), secondCorner.getZ());
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    @NotNull
    public World getWorldOrThrow() {
        return Objects.requireNonNull(getWorld(), "world");
    }

    public void forEach(Consumer<Location> consumer) {

        final World world = getWorldOrThrow();

        for (int x = (int) minX; x <= maxX; x++) {
            for (int y = (int) minY; y <= maxY; y++) {
                for (int z = (int) minZ; z <= maxZ; z++) {
                    consumer.accept(new Location(world, x, y, z));
                }
            }
        }
    }

    public Bucket<Location> asBucket(int size) {

        final World world = getWorldOrThrow();
        final Bucket<Location> bucket = BucketFactory.newHashSetBucket(size, PartitioningStrategies.nextInCycle());

        for (int x = (int) minX; x <= maxX; x++) {
            for (int y = (int) minY; y <= maxY; y++) {
                for (int z = (int) minZ; z <= maxZ; z++) {
                    bucket.add(new Location(world, x, y, z));
                }
            }
        }

        return bucket;
    }

    public BoundingBox innerBoundingBox(double diagonalSize) {

        final Location center = center();

        // Calculate half-length of the diagonal in 3D space
        final double halfDiagonal = diagonalSize / 2.0;
        final double halfSideLength = halfDiagonal / Math.sqrt(3);

        final Location newFirstCorner = new Location(
                getWorldOrThrow(),
                center.getX() - halfSideLength,
                center.getY() - halfSideLength,
                center.getZ() - halfSideLength
        );

        final Location newSecondCorner = new Location(
                getWorldOrThrow(),
                center.getX() + halfSideLength,
                center.getY() + halfSideLength,
                center.getZ() + halfSideLength
        );

        return new BoundingBox(newFirstCorner, newSecondCorner);
    }

    public Location center() {
        final World world = getWorldOrThrow();
        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double centerZ = (minZ + maxZ) / 2.0;
        return new Location(world, centerX, centerY, centerZ);
    }

    public boolean contains(final Location location) {
        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();
        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }
}

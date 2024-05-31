package fr.robotv2.api.bucket.factory;

import fr.robotv2.api.bucket.Bucket;
import fr.robotv2.api.bucket.partitioning.PartitioningStrategy;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A set of methods for creating {@link Bucket}s.
 */
public final class BucketFactory {

    public static <E> Bucket<E> newBucket(int size, PartitioningStrategy<E> strategy, Supplier<Set<E>> setSupplier) {
        return new SetSuppliedBucket<>(size, strategy, setSupplier);
    }

    public static <E> Bucket<E> newHashSetBucket(int size, PartitioningStrategy<E> strategy) {
        return new HashSetBucket<>(size, strategy);
    }

    public static <E> Bucket<E> newSynchronizedHashSetBucket(int size, PartitioningStrategy<E> strategy) {
        return new SynchronizedHashSetBucket<>(size, strategy);
    }

    public static <E> Bucket<E> newConcurrentBucket(int size, PartitioningStrategy<E> strategy) {
        return new ConcurrentBucket<>(size, strategy);
    }

    private BucketFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}

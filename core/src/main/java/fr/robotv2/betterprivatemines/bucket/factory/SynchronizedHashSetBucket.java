package fr.robotv2.betterprivatemines.bucket.factory;

import fr.robotv2.betterprivatemines.bucket.AbstractBucket;
import fr.robotv2.betterprivatemines.bucket.partitioning.PartitioningStrategy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class SynchronizedHashSetBucket<E> extends AbstractBucket<E> {
    SynchronizedHashSetBucket(int size, PartitioningStrategy<E> partitioningStrategy) {
        super(size, partitioningStrategy);
    }

    @Override
    protected Set<E> createSet() {
        return Collections.synchronizedSet(new HashSet<>());
    }
}

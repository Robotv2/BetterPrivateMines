package fr.robotv2.api.bucket.factory;

import fr.robotv2.api.bucket.partitioning.PartitioningStrategy;
import fr.robotv2.api.bucket.AbstractBucket;

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

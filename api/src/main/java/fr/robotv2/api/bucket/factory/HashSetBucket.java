package fr.robotv2.api.bucket.factory;

import fr.robotv2.api.bucket.AbstractBucket;
import fr.robotv2.api.bucket.partitioning.PartitioningStrategy;

import java.util.HashSet;
import java.util.Set;

class HashSetBucket<E> extends AbstractBucket<E> {
    HashSetBucket(int size, PartitioningStrategy<E> partitioningStrategy) {
        super(size, partitioningStrategy);
    }

    @Override
    protected Set<E> createSet() {
        return new HashSet<>();
    }
}

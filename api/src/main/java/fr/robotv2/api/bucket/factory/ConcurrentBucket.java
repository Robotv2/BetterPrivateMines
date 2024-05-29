package fr.robotv2.api.bucket.factory;

import fr.robotv2.api.bucket.AbstractBucket;
import fr.robotv2.api.bucket.partitioning.PartitioningStrategy;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ConcurrentBucket<E> extends AbstractBucket<E> {
    ConcurrentBucket(int size, PartitioningStrategy<E> strategy) {
        super(size, strategy);
    }

    @Override
    protected Set<E> createSet() {
        return ConcurrentHashMap.newKeySet();
    }
}

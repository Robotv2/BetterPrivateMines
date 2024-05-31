package fr.robotv2.api.bucket.factory;

import fr.robotv2.api.bucket.AbstractBucket;
import fr.robotv2.api.bucket.partitioning.PartitioningStrategy;

import java.util.Set;
import java.util.function.Supplier;

class SetSuppliedBucket<E> extends AbstractBucket<E> {
    private final Supplier<Set<E>> setSupplier;

    SetSuppliedBucket(int size, PartitioningStrategy<E> strategy, Supplier<Set<E>> setSupplier) {
        super(size, strategy);
        this.setSupplier = setSupplier;
    }

    @Override
    protected Set<E> createSet() {
        return this.setSupplier.get();
    }
}

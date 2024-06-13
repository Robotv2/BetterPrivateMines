package fr.robotv2.api.reset.impl;

import fr.robotv2.api.bucket.BucketPartition;
import fr.robotv2.api.bucket.Cycle;
import fr.robotv2.api.material.MineBlockSet;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.vector.Position;

import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class GradualResetTask extends TimerTask {

    private final PrivateMine mine;
    private final CompletableFuture<Void> future;
    private final Cycle<BucketPartition<Position>> vectors;
    private final MineBlockSet set;

    private boolean completed;

    public GradualResetTask(final PrivateMine mine, final CompletableFuture<Void> future) {
        this.mine = mine;
        this.future = future;
        this.vectors = mine.getMineableAreaBox().asBucket(mine.getConfiguration().getMaxBlockPerTick()).asCycle();
        this.completed = false;
        this.set = mine.getConfiguration().getLevelSet(mine.getLevel());
    }

    @Override
    public void run() {

        if(completed) {
            future.complete(null);
            cancel();
            return;
        }

        final BucketPartition<Position> partition = vectors.current();

        for(Position position : partition) {

            final MineMaterial mineMaterial = set.getRandomMaterial();

            if(mineMaterial != null) {
                mineMaterial.place(position);
            }
        }

        vectors.next();

        if (vectors.cursor() == 0) {
            completed = true;
        }
    }
}

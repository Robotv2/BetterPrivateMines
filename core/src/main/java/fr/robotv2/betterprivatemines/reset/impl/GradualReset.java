package fr.robotv2.betterprivatemines.reset.impl;

import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.bucket.BucketPartition;
import fr.robotv2.betterprivatemines.bucket.Cycle;
import fr.robotv2.betterprivatemines.mine.PrivateMine;
import fr.robotv2.betterprivatemines.reset.MineResetInterface;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

public enum GradualReset implements MineResetInterface {

    INSTANCE,
    ;

    @Override
    public CompletableFuture<Void> reset(final PrivateMine mine) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        new GradualResetRunnable(mine, future).runTaskTimer(BetterPrivateMines.instance(), 1L, 1L);
        return future;
    }

    public static class GradualResetRunnable extends BukkitRunnable {

        private final PrivateMine mine;
        private final CompletableFuture<Void> future;
        private final Cycle<BucketPartition<Location>> vectors;

        private boolean completed;

        public GradualResetRunnable(final PrivateMine mine, final CompletableFuture<Void> future) {
            this.mine = mine;
            this.future = future;
            this.vectors = mine.getMineableAreaBox().asBucket(mine.getConfiguration().getMaxBlockPerTick()).asCycle();
            this.completed = false;
        }

        @Override
        public void run() {

            final World world = mine.getEntireZoneBox().getWorld();

            if(world == null) {
                future.completeExceptionally(new NullPointerException("world"));
                cancel();
                return;
            }

            if(completed) {
                future.complete(null);
                cancel();
                return;
            }

            final BucketPartition<Location> partition = vectors.current();

            for(Location location : partition) {
                mine.getConfiguration().getRandomMaterial().place(mine, location);
            }

            vectors.next();

            if (vectors.cursor() == 0) {
                completed = true;
            }
        }
    }
}

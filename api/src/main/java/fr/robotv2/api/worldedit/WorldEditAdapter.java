package fr.robotv2.api.worldedit;

import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class WorldEditAdapter {

    private static WorldEditAdapter adapter;

    public static void setWorldEditAdapter(WorldEditAdapter adapter) {
        if(adapter != null) {
            throw new RuntimeException("Can't change adapter during runtime.");
        }
        WorldEditAdapter.adapter = adapter;
    }

    public static WorldEditAdapter getWorldEditAdapter() {
        Objects.requireNonNull(adapter, "WorldEditAdapter is null. Please install worldedit");
        return adapter;
    }

    public abstract <T> CompletableFuture<Void> fill(BoundingBox boundingBox, PrivateMineConfiguration<T> configuration);

    public abstract void pasteSchematic(File file, Position vector, CompletableFuture<BoundingBox> future);
}

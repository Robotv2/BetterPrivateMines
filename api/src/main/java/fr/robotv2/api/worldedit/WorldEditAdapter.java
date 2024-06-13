package fr.robotv2.api.worldedit;

import fr.robotv2.api.material.MineBlockSet;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class WorldEditAdapter<T> {

    private static WorldEditAdapter<?> adapter = null;
    protected final Function<MineMaterial, T> resolver;

    public WorldEditAdapter(Function<MineMaterial, T> resolver) {
        this.resolver = resolver;
    }

    public static <T> void setWorldEditAdapter(WorldEditAdapter<T> adapter) {

        if(WorldEditAdapter.adapter != null) {
            throw new RuntimeException("Can't change adapter during runtime.");
        }

        WorldEditAdapter.adapter = adapter;
    }

    @SuppressWarnings("unchecked")
    public static <T> WorldEditAdapter<T> getWorldEditAdapter() {
        Objects.requireNonNull(adapter, "WorldEditAdapter is null. Please install worldedit");
        return (WorldEditAdapter<T>) adapter;
    }

    public abstract void fillRandom(BoundingBox boundingBox, MineBlockSet set, CompletableFuture<Void> future);

    public abstract void fill(BoundingBox boundingBox, T material, CompletableFuture<Void> future);

    public abstract void pasteSchematic(File file, Position vector, CompletableFuture<BoundingBox> future);
}

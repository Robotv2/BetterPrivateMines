package fr.robotv2.api.storage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CompletableStorage<ID, T extends Identifiable<ID>> {

    CompletableFuture<Optional<T>> select(ID id);

    CompletableFuture<T> insert(T value);

    CompletableFuture<T> update(ID id, T value);

    CompletableFuture<Void> remove(T value);

    CompletableFuture<Void> removeFromId(ID id);

    void close();

    static <ID, T extends Identifiable<ID>> CompletableStorage<ID, T> wrap(Storage<ID, T> storageManager) {
        return new CompletableStorage<ID, T>() {
            @Override
            public CompletableFuture<Optional<T>> select(ID id) {
                return CompletableFuture.completedFuture(storageManager.select(id));
            }

            @Override
            public CompletableFuture<Void> insert(T value) {
                storageManager.insert(value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> update(ID id, T value) {
                storageManager.update(id, value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> remove(T value) {
                storageManager.remove(value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> removeFromId(ID id) {
                storageManager.removeFromId(id);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void close() {
                storageManager.close();
            }
        };
    }
}
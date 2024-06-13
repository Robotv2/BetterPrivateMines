package fr.robotv2.api.storage.impl;

import fr.robotv2.api.json.JsonHelper;
import fr.robotv2.api.storage.Identifiable;
import fr.robotv2.api.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PerValueFileStorage<ID, T extends Identifiable<ID>> extends JsonHelper<ID, T> implements Storage<ID, T> {

    private final Map<ID, T> valueCache = new ConcurrentHashMap<>();
    private final Map<ID, File> fileCache = new ConcurrentHashMap<>();

    private final File folder;
    private final Class<T> tClass;

    public PerValueFileStorage(File folder, Class<T> tClass) {

        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.folder = folder;
        this.tClass = tClass;
    }

    private File getFileFor(ID id) {
        return fileCache.computeIfAbsent(id, identification -> new File(folder, identification.toString() + ".json"));
    }

    @Override
    public Optional<T> select(ID id) {
        return Optional.ofNullable(valueCache.computeIfAbsent(id, k -> {
            synchronized (getFileFor(id)) {
                try {
                    return fromFile(getFileFor(id), tClass);
                } catch (IOException exception) {
                    throw new StorageException("Failed to load data with id " + id, exception);
                }
            }
        }));
    }

    @Override
    public List<T> selectAll() {

        final File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if(files == null || files.length == 0) {
            return Collections.emptyList();
        }

        final List<T> allValues = new ArrayList<>();

        for (File file : files) {

            try {
                allValues.add(fromFile(file, tClass));
            } catch (IOException exception) {
                throw new RuntimeException("Failed to load data with file named " + file.getName(), exception);
            }
        }

        return allValues;
    }

    @Override
    public void insert(T value) {
        synchronized (getFileFor(value.getId())) {
            try {
                writeToFile(getFileFor(value.getId()), value);
                valueCache.put(value.getId(), value);
            } catch (IOException exception) {
                throw new StorageException("An error occurred while inserting value with id " + value.getId(), exception);
            }
        }
    }

    @Override
    public void update(ID id, T value) {
        insert(value);
    }

    @Override
    public void remove(T value) {
        removeFromId(value.getId());
    }

    @Override
    public void removeFromId(ID id) {
        final File file = getFileFor(id);

        synchronized (file) {
            if (file.delete()) {
                valueCache.remove(id);
                fileCache.remove(id);
            } else {
                throw new StorageException("Failed to delete file for id " + id);
            }
        }
    }

    @Override
    public void close() {
        valueCache.clear();
        fileCache.clear();
    }

    public static class StorageException extends RuntimeException {

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }

        public StorageException(String message) {
            super(message);
        }
    }
}


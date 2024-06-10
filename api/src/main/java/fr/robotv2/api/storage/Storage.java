package fr.robotv2.api.storage;

import java.util.Optional;

public interface Storage<ID, T extends Identifiable<ID>> {

    Optional<T> select(ID id);

    void insert(T value);

    void update(ID id, T value);

    void remove(T value);

    void removeFromId(ID id);

    void close();

}

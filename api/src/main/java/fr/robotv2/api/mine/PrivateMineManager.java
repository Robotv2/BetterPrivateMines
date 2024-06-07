package fr.robotv2.api.mine;

import fr.robotv2.api.vector.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PrivateMineManager {

    private final Map<UUID, PrivateMine> mines = new ConcurrentHashMap<>();

    public void register(PrivateMine mine) {
        mines.put(mine.getMineId(), mine);
    }

    public void unregister(UUID mineId) {
        mines.remove(mineId);
    }

    public Optional<PrivateMine> getPrivateMine(@NotNull UUID mineId) {
        return Optional.ofNullable(mines.get(mineId));
    }

    @UnmodifiableView
    public Collection<PrivateMine> getPrivateMines() {
        return Collections.unmodifiableCollection(mines.values());
    }

    public Optional<PrivateMine> atPosition(Position position) {
        return getPrivateMines().stream().filter(mine -> mine.getEntireZoneBox().contains(position)).findFirst();
    }

    @UnmodifiableView
    public List<PrivateMine> ofPlayer(UUID playerId) {
        return getPrivateMines().stream().filter(mine -> Objects.equals(mine.getOwnerId(), playerId)).collect(Collectors.toList());
    }
}

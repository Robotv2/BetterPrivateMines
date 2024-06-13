package fr.robotv2.api.mine;

import fr.robotv2.api.BetterPrivateMinesPlugin;
import fr.robotv2.api.vector.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PrivateMineManager {

    private final BetterPrivateMinesPlugin plugin;
    private final Map<UUID, PrivateMine> mines = new ConcurrentHashMap<>();

    public PrivateMineManager(BetterPrivateMinesPlugin plugin) {
        this.plugin = plugin;
    }

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

    public Optional<PrivateMine> ofPlayer(UUID playerId, String mineName) {
        return ofPlayer(playerId).stream().filter(pv -> pv.getMineName().equalsIgnoreCase(mineName)).findFirst();
    }

    public boolean hasMine(UUID playerId, String mineName) {
        return ofPlayer(playerId, mineName).isPresent();
    }

    public void save(PrivateMine mine) {
        if(mine.needSaving()) {
            plugin.getPrivateMineStorage().update(mine.getMineId(), mine);
            mine.setNeedSaving(false);
        }
    }

    public void saveAll() {
        getPrivateMines().forEach(this::save);
    }
}

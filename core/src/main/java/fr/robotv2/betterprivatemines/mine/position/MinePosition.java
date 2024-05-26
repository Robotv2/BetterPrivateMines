package fr.robotv2.betterprivatemines.mine.position;

import com.google.common.collect.ArrayListMultimap;
import fr.robotv2.betterprivatemines.vector.BoundingBox;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public class MinePosition {

    private final ArrayListMultimap<MinePositionType, Location> positions = ArrayListMultimap.create();

    public void setPosition(MinePositionType type, Location location) {
        positions.put(type, location);
    }

    public Optional<Location> getFirst(MinePositionType type) {
        return positions.get(type).stream().findFirst();
    }

    public BoundingBox toBoundingBox(MinePositionType type) {
        final List<Location> locations = positions.get(type);
        return new BoundingBox(locations.get(0), locations.get(1));
    }

    public List<Location> getPositions(MinePositionType type) {
        return positions.get(type);
    }

    public boolean isValid() {

        if(positions.get(MinePositionType.MINE_CORNER).size() != 2) {
            return false;
        }

        return true;
    }
}

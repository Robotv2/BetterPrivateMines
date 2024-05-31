package fr.robotv2.api.position;

import com.google.common.collect.ArrayListMultimap;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;

import java.util.List;
import java.util.Optional;

public class MinePosition {

    private final ArrayListMultimap<MinePositionType, Position> positions = ArrayListMultimap.create();

    public void setPosition(MinePositionType type, Position position) {
        positions.put(type, position);
    }

    public Position getFirst(MinePositionType type) {
        return positions.get(type).stream().findFirst().orElseThrow(() -> new NullPointerException(type + " can't be empty."));
    }

    public BoundingBox toBoundingBox(MinePositionType type) {
        final List<Position> locations = positions.get(type);
        return new BoundingBox(locations.get(0), locations.get(1));
    }

    public List<Position> getPositions(MinePositionType type) {
        return positions.get(type);
    }

    public boolean isValid() {

        for(MinePositionType type : MinePositionType.VALUES) {
            if(type.getExpected() > -1 && positions.get(type).size() != type.getExpected()) {
                return false;
            }
        }

        return true;
    }
}

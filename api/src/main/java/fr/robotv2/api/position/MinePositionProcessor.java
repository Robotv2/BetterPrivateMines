package fr.robotv2.api.position;

import fr.robotv2.api.vector.BoundingBox;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;

@UtilityClass
@Log
public class MinePositionProcessor {

    public <T> MinePosition fromBoundingBox(BoundingBox boundingBox, MinePositionProcessorConfig<T> config) {

        final MinePosition minePosition = new MinePosition();

        log.info("Creating mine from bounding box: " + boundingBox);

        boundingBox.forEach(position -> {

            final T value = config.getFunction().apply(position);

            if(value == null || value.toString().equalsIgnoreCase("AIR")) {
                return;
            }

            final MinePositionType positionType = config.getTypes().get(value);

            if(positionType == null) {
                return;
            }

            log.info(value + " is a position for " + positionType + " !");
            minePosition.setPosition(positionType, position);
        });

        if(!minePosition.isValid()) {
            throw new IllegalArgumentException("Can't create mine position for given bounding box contain not enough / too much valid blocks.");
        }

        return minePosition;
    }
}

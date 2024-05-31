package fr.robotv2.api.grid.impl;

import fr.robotv2.api.grid.GridPosition;
import fr.robotv2.api.grid.GridPositionResolver;

public class HelixGridPositionResolver implements GridPositionResolver {

    // default values
    private int step = 0;
    private int direction = 0;
    private int distance = 1;
    private int x = 0;
    private int z = 0;
    private int dx = 1;
    private int dz = 0;

//    public static HelixPositionResolver fromState(JsonElement element) {
//
//        final HelixPositionResolver resolver = new HelixPositionResolver();
//
//        if(element == null) {
//            return resolver;
//        }
//
//        final JsonObject json = element.getAsJsonObject();
//
//        resolver.step = json.get("step").getAsInt();
//        resolver.direction = json.get("direction").getAsInt();
//        resolver.distance = json.get("distance").getAsInt();
//        resolver.x = json.get("x").getAsInt();
//        resolver.z = json.get("z").getAsInt();
//        resolver.dx = json.get("dx").getAsInt();
//        resolver.dz = json.get("dz").getAsInt();
//
//        return resolver;
//    }

    @Override
    public GridPosition next(GridPosition lastPos) {

        if (lastPos == null) {
            return new GridPosition(x, z);
        }

        step++;

        if (step == distance) {
            step = 0;
            int temp = dx;
            dx = -dz;
            dz = temp;

            direction++;
            if (direction % 2 == 0) {
                distance++;
            }
        }

        x += dx;
        z += dz;

        return new GridPosition(x, z);
    }
}

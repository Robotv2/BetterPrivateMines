package fr.robotv2.api.grid.impl;

import fr.robotv2.api.grid.GridPosition;
import fr.robotv2.api.grid.GridPositionResolver;

public class HelixGridPositionResolver implements GridPositionResolver {

    public static final int BLOCK_DISTANCE = 150;

    // default values
    private int step = 0;
    private int direction = 0;
    private int distance = 1; // Distance in terms of steps, not blocks
    private int x = 0;
    private int z = 0;
    private int dx = 1;
    private int dz = 0;

    @Override
    public GridPosition next(GridPosition lastPos) {

        if (lastPos == null) {
            return new GridPosition(x, z);
        }

        step++;

        // Increment x and z based on current direction
        x += dx * BLOCK_DISTANCE;
        z += dz * BLOCK_DISTANCE;

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

        return new GridPosition(x, z);
    }
}




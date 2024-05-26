package fr.robotv2.betterprivatemines.schematic.placement;

public interface PositionResolver {
    GridPosition next(GridPosition lastPos);
}

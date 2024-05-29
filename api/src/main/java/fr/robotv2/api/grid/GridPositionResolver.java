package fr.robotv2.api.grid;

public interface GridPositionResolver {
    GridPosition next(GridPosition lastPos);
}

package fr.robotv2.api.position;

import fr.robotv2.api.vector.Position;
import lombok.Data;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class MinePositionProcessorConfig<T> {

    private final Map<T, MinePositionType> types;

    private final Function<Position, T> positionToValueFunction;

    private final Consumer<Position> clearPositionConsumer;
}

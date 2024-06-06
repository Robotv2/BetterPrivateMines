package fr.robotv2.api.mine;

import fr.robotv2.api.grid.Grid;
import fr.robotv2.api.grid.GridPosition;
import fr.robotv2.api.position.MinePosition;
import fr.robotv2.api.position.MinePositionProcessor;
import fr.robotv2.api.position.MinePositionProcessorConfig;
import fr.robotv2.api.schematic.SchematicManager;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;
import fr.robotv2.api.world.WorldManager;
import fr.robotv2.api.worldedit.WorldEditAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@AllArgsConstructor
@Log
public final class PrivateMineFactory {

    public static final int Y_POSITION = 30;

    private final PrivateMineManager privateMineManager;

    private final SchematicManager schematicManager;

    private final WorldManager worldManager;

    private final Grid grid;

    public CompletableFuture<PrivateMine> newPrivateMine(UUID playerId, PrivateMineConfiguration<?> configuration, MinePositionProcessorConfig<?> processorConfig) {

        final GridPosition gridPosition = grid.next();
        final Position position = Position.of(worldManager.getMineWorldName(), gridPosition.getX(), Y_POSITION, gridPosition.getZ());

        final CompletableFuture<BoundingBox> future = new CompletableFuture<>();

        WorldEditAdapter.getWorldEditAdapter().pasteSchematic(schematicManager.getSchematic(configuration.getSchematicName()),position, future);

        return future.thenApply(boundingBox -> {

            final MinePosition minePosition = MinePositionProcessor.fromBoundingBox(boundingBox, processorConfig);
            final PrivateMine privateMine = new PrivateMine(playerId, configuration, minePosition, boundingBox);
            privateMineManager.register(privateMine);

            return privateMine;
        }).exceptionally(throwable -> {
            log.log(Level.SEVERE, "An error occurred while pasting schematic from file: " + configuration.getSchematicName(), throwable);
            return null;
        });
    }
}

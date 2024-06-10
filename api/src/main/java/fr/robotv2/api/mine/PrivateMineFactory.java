package fr.robotv2.api.mine;

import fr.robotv2.api.BetterPrivateMinesPlugin;
import fr.robotv2.api.grid.GridPosition;
import fr.robotv2.api.position.MinePosition;
import fr.robotv2.api.position.MinePositionProcessor;
import fr.robotv2.api.position.MinePositionProcessorConfig;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;
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

    private final BetterPrivateMinesPlugin plugin;

    public CompletableFuture<PrivateMine> newPrivateMine(UUID playerId, PrivateMineConfiguration<?> configuration, MinePositionProcessorConfig<?> processorConfig) {

        final GridPosition gridPosition = plugin.getGridManager().getGrid().next();
        final Position position = Position.of(plugin.getWorldManager().getMineWorldName(), gridPosition.getX(), Y_POSITION, gridPosition.getZ());

        final CompletableFuture<BoundingBox> future = new CompletableFuture<>();

        WorldEditAdapter.getWorldEditAdapter().pasteSchematic(plugin.getSchematicManager().getSchematic(configuration.getSchematicName()),position, future);

        return future.thenApply(boundingBox -> {

            final MinePosition minePosition = MinePositionProcessor.fromBoundingBox(boundingBox, processorConfig);
            final PrivateMine privateMine = new PrivateMine(
                    playerId,
                    configuration.getConfigurationName(),
                    plugin.getPrivateMineConfigurationManager(),
                    minePosition,
                    boundingBox
            );

            plugin.getPrivateMineManager().register(privateMine);
            return privateMine;

        }).thenCompose(plugin.getPrivateMineStorage()::insert).exceptionally(throwable -> {
            log.log(Level.SEVERE, "An error occurred while pasting schematic from file: " + configuration.getSchematicName(), throwable);
            return null;
        });
    }
}

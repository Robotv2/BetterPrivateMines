package fr.robotv2.betterprivatemines.mine;

import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.mine.position.MinePosition;
import fr.robotv2.betterprivatemines.mine.position.MinePositionFactory;
import fr.robotv2.betterprivatemines.schematic.placement.GridPosition;
import fr.robotv2.betterprivatemines.vector.BoundingBox;
import fr.robotv2.betterprivatemines.worldedit.WorldEditAdapter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@UtilityClass
public class PrivateMineFactory {

    private final BetterPrivateMines PLUGIN = BetterPrivateMines.instance();

    public CompletableFuture<PrivateMine> newPrivateMine(UUID ownerId, PrivateMineConfiguration configuration) {

        final GridPosition gridPosition = PLUGIN.getSchematicManager().getSchematicPlacement().next();

        if(gridPosition == null) {
            throw new NullPointerException("grid position");
        }

        final CompletableFuture<BoundingBox> future = new CompletableFuture<>();
        final Location location = new Location(PLUGIN.getWorldManager().getWorld(), gridPosition.getX(), 50, gridPosition.getZ());
        WorldEditAdapter.getWorldEditAdapter().pasteSchematic(configuration.getMineSchematic(), location, future);

        return future.thenApply(box -> {

            final MinePosition minePosition = MinePositionFactory.fromBoundingBox(box, configuration.getPositions().inverse());
            final PrivateMine privateMine = new PrivateMine(ownerId, configuration, minePosition, box);

            return privateMine;
        }).exceptionally(throwable -> {
            BetterPrivateMines.logger().log(Level.SEVERE, "An error occurred while pasting schematic from file " + configuration.getSchematicName(), throwable);
            return null;
        });
    }
}

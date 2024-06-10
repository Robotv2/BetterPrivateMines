package fr.robotv2.api;

import fr.robotv2.api.grid.GridManager;
import fr.robotv2.api.mine.*;
import fr.robotv2.api.schematic.SchematicManager;
import fr.robotv2.api.storage.CompletableStorage;
import fr.robotv2.api.world.WorldManager;

import java.util.UUID;

public interface BetterPrivateMinesPlugin {

    WorldManager getWorldManager();

    SchematicManager getSchematicManager();

    GridManager getGridManager();

    PrivateMineManager getPrivateMineManager();

    CompletableStorage<UUID, PrivateMine> getPrivateMineStorage();

    PrivateMineConfigurationManager<?> getPrivateMineConfigurationManager();
}

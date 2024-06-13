package fr.robotv2.api;

import com.google.common.base.Preconditions;
import fr.robotv2.api.grid.GridManager;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.mine.PrivateMineConfigurationManager;
import fr.robotv2.api.mine.PrivateMineManager;
import fr.robotv2.api.schematic.SchematicManager;
import fr.robotv2.api.storage.CompletableStorage;
import fr.robotv2.api.world.WorldManager;

import java.util.UUID;

public interface BetterPrivateMinesPlugin {

    static BetterPrivateMinesPlugin setInstance(BetterPrivateMinesPlugin instance) {
        return BetterPrivateMinesPluginHolder.set(instance);
    }

    static BetterPrivateMinesPlugin getInstance() {
        return BetterPrivateMinesPluginHolder.get();
    }

    WorldManager getWorldManager();

    SchematicManager getSchematicManager();

    GridManager getGridManager();

    PrivateMineManager getPrivateMineManager();

    CompletableStorage<UUID, PrivateMine> getPrivateMineStorage();

    PrivateMineConfigurationManager<?> getConfigurationManager();

    // Nested static class to hold the instance and provide thread-safe access
    final class BetterPrivateMinesPluginHolder {

        private static volatile BetterPrivateMinesPlugin instance;

        private BetterPrivateMinesPluginHolder() {}

        static synchronized BetterPrivateMinesPlugin set(BetterPrivateMinesPlugin newInstance) {
            Preconditions.checkArgument(instance == null, "can't modify bpv instance during runtime.");
            instance = newInstance;
            return instance;
        }

        static BetterPrivateMinesPlugin get() {
            return instance;
        }
    }
}

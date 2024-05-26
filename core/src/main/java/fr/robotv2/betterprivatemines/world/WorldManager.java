package fr.robotv2.betterprivatemines.world;

import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.util.EmptyWorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class WorldManager {

    private final BetterPrivateMines plugin;

    private WeakReference<World> worldReference;

    public WorldManager(BetterPrivateMines plugin) {
        this.plugin = plugin;
        generateMineWorld();
    }

    @NotNull
    public World getWorld() {
        final World world = worldReference.get();

        if(world == null) {
            generateMineWorld();
            return getWorld();
        }

        return world;
    }

    public void generateMineWorld() {
        final String dungeonWorldName = plugin.getConfig().getString("mine_world.name", "prison_world");
        final WorldCreator creator = new WorldCreator(dungeonWorldName).type(WorldType.FLAT).generator(new EmptyWorldGenerator());
        final World world = Bukkit.createWorld(creator);
        this.worldReference =  new WeakReference<>(world);
    }
}

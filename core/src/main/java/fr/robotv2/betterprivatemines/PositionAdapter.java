package fr.robotv2.betterprivatemines;

import fr.robotv2.api.vector.Position;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PositionAdapter {

    @Contract("_ -> new")
    @NotNull
    public Location fromPosition(@NotNull Position position) {
        return new Location(Bukkit.getWorld(position.getWorldName()), position.getX(), position.getY(), position.getZ());
    }

    @Contract("_ -> new")
    @NotNull
    public Position toPosition(@NotNull Location location) {
        return Position.of(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }
}

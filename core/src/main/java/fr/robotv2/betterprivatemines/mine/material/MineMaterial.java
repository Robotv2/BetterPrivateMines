package fr.robotv2.betterprivatemines.mine.material;

import fr.robotv2.betterprivatemines.mine.PrivateMine;
import org.bukkit.Location;

public interface MineMaterial {

    void place(final PrivateMine mine, final Location location);

    static MineMaterial resolve(String literal) {
        return InternalMineMaterial.from(literal);
    }
}

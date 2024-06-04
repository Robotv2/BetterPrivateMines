package fr.robotv2.api.material;

import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.vector.Position;

public interface MineMaterial {

    void place(final PrivateMine mine, final Position location);

    String worldEditLiteral();
}

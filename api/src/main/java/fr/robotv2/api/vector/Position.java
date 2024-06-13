package fr.robotv2.api.vector;

import lombok.Data;
import org.jetbrains.annotations.Contract;

@Data(staticConstructor = "of")
public class Position implements java.io.Serializable {

    private final String worldName;
    private final double X;
    private final double Y;
    private final double Z;

    @Contract("_, _, _ -> new")
    public Position subtract(double x, double y, double z) {
        return Position.of(worldName, (this.X - x), (this.Y - y), (this.Z - z));
    }

    @Contract("_, _, _ -> new")
    public Position add(double x, double y, double z) {
        return Position.of(worldName, (this.X + x), (this.Y + y), (this.Z + z));
    }

    @Contract("-> new")
    public Position center() {
        return Position.of(worldName, this.X / 2, this.Y / 2, this.Z / 2);
    }
}

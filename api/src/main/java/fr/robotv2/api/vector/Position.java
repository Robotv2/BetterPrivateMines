package fr.robotv2.api.vector;

import lombok.Data;

@Data(staticConstructor = "of")
public class Position implements java.io.Serializable {

    private final String worldName;
    private final double X;
    private final double Y;
    private final double Z;

}

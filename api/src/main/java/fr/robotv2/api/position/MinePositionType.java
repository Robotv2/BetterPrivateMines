package fr.robotv2.api.position;

import lombok.Getter;

@Getter
public enum MinePositionType {

    MINE_CORNER(2),
    SPAWN_POINT(1),
    ;

    public static final MinePositionType[] VALUES = values();
    private final int expected;

    MinePositionType(int expected) {
        this.expected = expected;
    }

    public String lower() {
        return name().toLowerCase();
    }
}

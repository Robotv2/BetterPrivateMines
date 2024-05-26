package fr.robotv2.betterprivatemines.mine.position;

public enum MinePositionType {

    MINE_CORNER,
    ;

    public static final MinePositionType[] VALUES = values();

    public String lower() {
        return name().toLowerCase();
    }
}

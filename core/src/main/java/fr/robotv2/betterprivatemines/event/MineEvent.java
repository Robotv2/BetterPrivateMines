package fr.robotv2.betterprivatemines.event;

import fr.robotv2.api.mine.PrivateMine;
import org.bukkit.event.Event;

public abstract class MineEvent extends Event {

    private final PrivateMine privateMine;

    protected MineEvent(PrivateMine privateMine) {
        this.privateMine = privateMine;
    }
}

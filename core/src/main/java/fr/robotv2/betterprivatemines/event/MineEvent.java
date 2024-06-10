package fr.robotv2.betterprivatemines.event;

import fr.robotv2.api.mine.PrivateMine;
import lombok.Getter;
import org.bukkit.event.Event;

@Getter
public abstract class MineEvent extends Event {

    private final PrivateMine privateMine;

    protected MineEvent(PrivateMine privateMine) {
        this.privateMine = privateMine;
    }
}

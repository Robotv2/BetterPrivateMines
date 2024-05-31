package fr.robotv2.betterprivatemines.event;

import fr.robotv2.api.mine.PrivateMine;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
public class MineLeaveEvent extends MineEvent implements Cancellable {

    private final HandlerList HANDLER_LIST = new HandlerList();
    private final Player who;
    private final LeaveManner manner;

    private boolean cancel = false;

    public MineLeaveEvent(Player who, PrivateMine privateMine, LeaveManner manner) {
        super(privateMine);
        this.who = who;
        this.manner = manner;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public enum LeaveManner {

        BORDER,
        TELEPORT,
        DISCONNECTION,
        ;

    }
}

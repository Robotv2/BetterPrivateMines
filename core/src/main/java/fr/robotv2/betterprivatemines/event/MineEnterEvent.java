package fr.robotv2.betterprivatemines.event;

import fr.robotv2.api.mine.PrivateMine;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
public class MineEnterEvent extends MineEvent implements Cancellable {

    private final HandlerList HANDLER_LIST = new HandlerList();
    private final Player who;
    private final EnterManner manner;

    private boolean cancel = false;

    public MineEnterEvent(Player who, PrivateMine privateMine, EnterManner manner) {
        super(privateMine);
        this.who = who;
        this.manner = manner;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum EnterManner {

        BORDER,
        TELEPORT,
        CONNECTION,
        ;

    }
}

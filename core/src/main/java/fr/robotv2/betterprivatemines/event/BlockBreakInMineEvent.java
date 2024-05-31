package fr.robotv2.betterprivatemines.event;

import fr.robotv2.api.mine.PrivateMine;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
public class BlockBreakInMineEvent extends MineEvent implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private final Player who;
    private final Block block;

    private boolean cancel = false;

    public BlockBreakInMineEvent(Player who, PrivateMine privateMine, Block block) {
        super(privateMine);
        this.who = who;
        this.block = block;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}

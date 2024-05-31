package fr.robotv2.betterprivatemines.listener;

import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.vector.Position;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.event.BlockBreakInMineEvent;
import fr.robotv2.betterprivatemines.event.MineEnterEvent;
import fr.robotv2.betterprivatemines.event.MineLeaveEvent;
import fr.robotv2.betterprivatemines.util.PositionAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

@RequiredArgsConstructor
public class SystemListeners implements Listener {

    private final BetterPrivateMines plugin;

    @EventHandler(priority = EventPriority.MONITOR) // in case player is teleported on connection
    public void onJoin(final PlayerJoinEvent event) {

        final Position position = PositionAdapter.toPosition(event.getPlayer().getLocation());
        final Optional<PrivateMine> optional = plugin.getPrivateMineManager().atPosition(position);

        if(optional.isPresent()) {
            MineEnterEvent mineEnteredEvent = new MineEnterEvent(event.getPlayer(), optional.get(), MineEnterEvent.EnterManner.CONNECTION);
            Bukkit.getPluginManager().callEvent(mineEnteredEvent);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onMoveFullXYZEvent(final PlayerMoveEvent event) {

        final double blockXFrom = event.getFrom().getX();
        final double blockYFrom = event.getFrom().getY();
        final double blockZFrom = event.getFrom().getZ();

        final double blockXTo = event.getTo().getX();
        final double blockYTo = event.getTo().getY();
        final double blockZTo = event.getTo().getZ();

        if (blockXFrom != blockXTo || blockYFrom != blockYTo || blockZFrom != blockZTo) {

            final Optional<PrivateMine> optionalMineFrom = plugin.getPrivateMineManager().atPosition(PositionAdapter.toPosition(event.getFrom()));
            final Optional<PrivateMine> optionalMineTo = plugin.getPrivateMineManager().atPosition(PositionAdapter.toPosition(event.getTo()));

            if (optionalMineFrom.isPresent() && !optionalMineTo.isPresent()) {

                MineLeaveEvent mineLeaveEvent = new MineLeaveEvent(event.getPlayer(), optionalMineFrom.get(), MineLeaveEvent.LeaveManner.BORDER);
                Bukkit.getPluginManager().callEvent(mineLeaveEvent);

                if (mineLeaveEvent.isCancelled()) {
                    event.setCancelled(true);
                }

            } else if (!optionalMineFrom.isPresent() && optionalMineTo.isPresent()) {

                MineEnterEvent mineEnteredEvent = new MineEnterEvent(event.getPlayer(), optionalMineTo.get(), MineEnterEvent.EnterManner.BORDER);
                Bukkit.getPluginManager().callEvent(mineEnteredEvent);

                if (mineEnteredEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBreak(final BlockBreakEvent event) {

        final Position blockPosition = PositionAdapter.toPosition(event.getBlock().getLocation());
        final Optional<PrivateMine> optional = plugin.getPrivateMineManager().atPosition(blockPosition);

        if(optional.isPresent() && optional.get().getMineableAreaBox().contains(blockPosition)) {

            final BlockBreakInMineEvent blockBreakInMineEvent = new BlockBreakInMineEvent(event.getPlayer(), optional.get(), event.getBlock());
            Bukkit.getPluginManager().callEvent(blockBreakInMineEvent);

            if(blockBreakInMineEvent.isCancel()) {
                event.setCancelled(true);
            }
        }
    }
}

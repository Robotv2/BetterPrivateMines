package fr.robotv2.betterprivatemines.listener;

import fr.robotv2.betterprivatemines.event.MineEnterEvent;
import fr.robotv2.betterprivatemines.event.MineLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MineEnterQuitListener implements Listener {

    @EventHandler
    public void onMineEnter(final MineEnterEvent event) {
        event.getPlayer().sendMessage(ChatColor.GREEN + "Vous venez de rentrer dans la mine de " + Bukkit.getOfflinePlayer(event.getPrivateMine().getOwnerId()).getName());
    }

    @EventHandler
    public void onMineLeave(final MineLeaveEvent event) {
        event.getPlayer().sendMessage(ChatColor.RED + "Vous venez de quitter dans la mine de " + Bukkit.getOfflinePlayer(event.getPrivateMine().getOwnerId()).getName());
    }
}

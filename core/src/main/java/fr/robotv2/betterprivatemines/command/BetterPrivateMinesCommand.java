package fr.robotv2.betterprivatemines.command;

import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.position.MinePositionType;
import fr.robotv2.api.vector.Position;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.config.BukkitPrivateMineConfiguration;
import fr.robotv2.betterprivatemines.util.PositionAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Command({"privatemines", "privatemine", "pv"})
@RequiredArgsConstructor
public class BetterPrivateMinesCommand {

    private final BetterPrivateMines plugin;

    @Subcommand("reload")
    @CommandPermission("betterprivatemines.command.reload")
    public void onReload(final BukkitCommandActor actor) {
        plugin.onReload();
        actor.getSender().sendMessage(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("create")
    @CommandPermission("betterprivatemines.command.create")
    @AutoComplete("@mines")
    public void onPrivateMineCreate(final BukkitCommandActor actor, @Named("configuration name") final String configName, @Optional @Named("target") Player target) {

        final Player player = target != null ? target : actor.requirePlayer();
        final BukkitPrivateMineConfiguration configuration = plugin.getConfigurationManager().getConfiguration(configName);

        if(configuration == null) {
            player.sendMessage(ChatColor.RED + "No mine's configuration exist with this name.");
            return;
        }

        plugin.getPrivateMineFactory().newPrivateMine(player.getUniqueId(), configuration, configuration.getMineProcessorConfig())
                .thenAccept(privateMine -> {
                    actor.getSender().sendMessage(ChatColor.GREEN + "The private mine was created successfully for " + player.getName() + ".");
                    plugin.getPrivateMineManager().register(privateMine);
                }).exceptionally(throwable -> {
                    actor.getSender().sendMessage(ChatColor.RED + "An error occurred while creating the private mine. Please contact an admin.");
                    plugin.getLogger().log(Level.SEVERE, "An error occurred while creating a private mine.", throwable);
                    return null;
                });
    }

    @Subcommand("teleport")
    @CommandPermission("betterprivatemines.command.teleport")
    public void onPrivateMineTeleport(final BukkitCommandActor actor, @Named("player") @Optional OfflinePlayer offlinePlayer, @Named("configuration name") @Optional String mineName) {

        final UUID ownerId = offlinePlayer != null ? offlinePlayer.getUniqueId() : actor.requirePlayer().getUniqueId();
        final List<PrivateMine> mines = plugin.getPrivateMineManager().ofPlayer(ownerId);

        if(mines.isEmpty()) {
            actor.getSender().sendMessage(ChatColor.RED + "No mine found.");
            return;
        }

        if(mines.size() == 1) {
            final PrivateMine privateMine = mines.get(0);
            final Position position = privateMine.getMinePosition().getFirst(MinePositionType.SPAWN_POINT);
            actor.requirePlayer().teleport(PositionAdapter.fromPosition(position));
            return;
        }

        actor.getSender().sendMessage(ChatColor.GREEN + "The player has more than one mine. This is not implemented yet.");
    }

    @Subcommand("reset")
    @CommandPermission("betterprivatemines.command.reset")
    public void onPrivateMineReset(final BukkitCommandActor actor) {

        final Position position = PositionAdapter.toPosition(actor.requirePlayer().getLocation());
        final java.util.Optional<PrivateMine> optional = plugin.getPrivateMineManager().atPosition(position);

        if(!optional.isPresent()) {
            actor.getSender().sendMessage(ChatColor.RED + "You must be in your private mine to do this.");
            return;
        }

        final PrivateMine privateMine = optional.get();

        privateMine.reset();
        actor.getSender().sendMessage(ChatColor.GREEN + "Reset en cours...");
    }

    @Subcommand("expand")
    @CommandPermission("betterprivatemines.command.expand")
    public void onPrivateMineExpand(final BukkitCommandActor actor, @Default("0") @Range(min = 1) int size) {

        final Position position = PositionAdapter.toPosition(actor.requirePlayer().getLocation());
        final java.util.Optional<PrivateMine> optional = plugin.getPrivateMineManager().atPosition(position);

        if(!optional.isPresent()) {
            actor.getSender().sendMessage(ChatColor.RED + "You must be in your private mine to do this.");
            return;
        }

        final PrivateMine privateMine = optional.get();

        privateMine.expand(size);
        actor.getSender().sendMessage(ChatColor.GREEN + "You just expand your mine by " + size);
    }
}

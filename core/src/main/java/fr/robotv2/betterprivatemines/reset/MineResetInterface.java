package fr.robotv2.betterprivatemines.reset;

import fr.robotv2.betterprivatemines.mine.PrivateMine;

import java.util.concurrent.CompletableFuture;

public interface MineResetInterface {
    CompletableFuture<Void> reset(final PrivateMine mine);
}

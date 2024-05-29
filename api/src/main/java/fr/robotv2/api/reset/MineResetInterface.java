package fr.robotv2.api.reset;

import fr.robotv2.api.mine.PrivateMine;

import java.util.concurrent.CompletableFuture;

public interface MineResetInterface {

    CompletableFuture<Void> reset(final PrivateMine mine);

}

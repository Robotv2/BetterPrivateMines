package fr.robotv2.betterprivatemines.reset.impl;

import fr.robotv2.betterprivatemines.mine.PrivateMine;
import fr.robotv2.betterprivatemines.reset.MineResetInterface;
import fr.robotv2.betterprivatemines.worldedit.WorldEditAdapter;

import java.util.concurrent.CompletableFuture;

public enum FullReset implements MineResetInterface {

    INSTANCE,
    ;

    @Override
    public CompletableFuture<Void> reset(PrivateMine mine) {
        return WorldEditAdapter.getWorldEditAdapter().fill(mine.getMineableAreaBox());
    }
}

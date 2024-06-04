package fr.robotv2.api.reset;

import com.google.common.base.Enums;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.reset.impl.GradualResetTask;
import fr.robotv2.api.worldedit.WorldEditAdapter;

import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public enum ResetType implements MineResetInterface {

    FULL {

        @Override
        public CompletableFuture<Void> reset(PrivateMine mine) {
            return WorldEditAdapter.getWorldEditAdapter().fill(mine.getMineableAreaBox(), mine.getConfiguration());
        }

    },
    GRADUAL {

        @Override
        public CompletableFuture<Void> reset(PrivateMine mine) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            new Timer().scheduleAtFixedRate(new GradualResetTask(mine, future), 50, 50);
            return future;
        }

    },
    ;

    public static ResetType resolveType(final String literal, final ResetType defaultType) {
        if(literal == null || literal.trim().isEmpty()) return defaultType;
        return Enums.getIfPresent(ResetType.class, literal.toUpperCase()).or(defaultType);
    }
}

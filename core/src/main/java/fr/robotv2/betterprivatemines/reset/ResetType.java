package fr.robotv2.betterprivatemines.reset;

import com.google.common.base.Enums;
import fr.robotv2.betterprivatemines.mine.PrivateMine;
import fr.robotv2.betterprivatemines.reset.impl.FullReset;
import fr.robotv2.betterprivatemines.reset.impl.GradualReset;

import java.util.concurrent.CompletableFuture;

public enum ResetType implements MineResetInterface {

    FULL {
        @Override
        public CompletableFuture<Void> reset(PrivateMine mine) {
            return FullReset.INSTANCE.reset(mine);
        }
    },
    GRADUAL {
        @Override
        public CompletableFuture<Void> reset(PrivateMine mine) {
            return GradualReset.INSTANCE.reset(mine);
        }
    },
    ;

    public static ResetType resolveType(final String literal, final ResetType defaultType) {
        if(literal == null || literal.trim().isEmpty()) return defaultType;
        return Enums.getIfPresent(ResetType.class, literal.toUpperCase()).or(defaultType);
    }
}

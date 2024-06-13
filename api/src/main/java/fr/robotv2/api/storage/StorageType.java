package fr.robotv2.api.storage;

import com.google.common.base.Enums;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum StorageType {

    JSON,
    MYSQL,
    SQLITE,
    ;

    public static StorageType resolveType(@Nullable String typeLiteral, @NotNull StorageType defaultType) {
        if(typeLiteral == null) return defaultType;
        return Enums.getIfPresent(StorageType.class, typeLiteral).or(defaultType);
    }
}

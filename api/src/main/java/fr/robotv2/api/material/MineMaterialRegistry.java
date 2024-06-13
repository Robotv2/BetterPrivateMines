package fr.robotv2.api.material;

import com.google.common.base.Function;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class MineMaterialRegistry {

    private final Map<String, Function<String, MineMaterial>> providers = new HashMap<>();

    @NotNull
    public MineMaterial resolve(@NotNull String key) {
        final String[] args = key.split("_");

        if(args.length <= 1) {
            return providers.get("internal".toUpperCase()).apply(key);
        }

        final Function<String, MineMaterial> function = providers.get(args[0].toUpperCase());

        if(function == null) {
            throw new IllegalArgumentException(args[0] + " is not a valid material provider");
        }

        return function.apply(key);
    }

    public void registerProvider(String providerId, Function<String, MineMaterial> function) {
        providers.put(providerId.toUpperCase(), function);
    }
}

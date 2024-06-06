package fr.robotv2.betterprivatemines.util;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class MaterialUtil {
    @NotNull
    public XMaterial matchXMaterialOrThrow(@NotNull String literal) {
        return XMaterial.matchXMaterial(literal.toUpperCase()).orElseThrow(() -> new NullPointerException(literal + " is not a valid material type."));
    }
}

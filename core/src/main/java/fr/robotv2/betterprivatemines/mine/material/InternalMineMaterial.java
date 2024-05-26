package fr.robotv2.betterprivatemines.mine.material;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.mine.PrivateMine;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class InternalMineMaterial implements MineMaterial {

    public final static InternalMineMaterial INTERNAL_AIR = new InternalMineMaterial(XMaterial.AIR);

    private final XMaterial material;

    public static InternalMineMaterial from(final XMaterial material) {
        return new InternalMineMaterial(material);
    }

    public static InternalMineMaterial from(final String literal) {
        return from(XMaterial.matchXMaterial(literal).orElseThrow(() -> new NullPointerException(literal + " not a valid material.")));
    }

    @Override
    public void place(final PrivateMine mine, final Location location) {
        if(!Bukkit.isPrimaryThread()) Bukkit.getScheduler().runTask(BetterPrivateMines.instance(), () -> place(mine, location));
        location.getBlock().setType(material.parseMaterial(), false);
    }
}

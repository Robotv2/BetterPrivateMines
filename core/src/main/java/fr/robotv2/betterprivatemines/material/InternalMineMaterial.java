package fr.robotv2.betterprivatemines.material;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.vector.Position;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.betterprivatemines.PositionAdapter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

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
    public void place(PrivateMine mine, Position position) {
        if(!Bukkit.isPrimaryThread()) Bukkit.getScheduler().runTask(BetterPrivateMines.instance(), () -> place(mine, position));
        PositionAdapter.fromPosition(position).getBlock().setType(material.parseMaterial(), false);
    }
}

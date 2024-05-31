package fr.robotv2.betterprivatemines.material;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.vector.Position;
import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.util.MaterialUtil;
import fr.robotv2.betterprivatemines.util.PositionAdapter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class BukkitMineMaterial implements MineMaterial {

    private final XMaterial material;

    public static BukkitMineMaterial from(final XMaterial material) {
        return new BukkitMineMaterial(material);
    }

    public static BukkitMineMaterial from(final String literal) {
        return from(MaterialUtil.matchXMaterialOrThrow(literal));
    }

    @Override
    public void place(PrivateMine mine, Position position) {

        if(!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(BetterPrivateMines.instance(), () -> place(mine, position));
            return;
        }

        PositionAdapter.fromPosition(position).getBlock().setType(material.parseMaterial(), false);
    }
}

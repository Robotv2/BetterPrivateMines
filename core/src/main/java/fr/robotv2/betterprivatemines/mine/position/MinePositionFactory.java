package fr.robotv2.betterprivatemines.mine.position;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.betterprivatemines.vector.BoundingBox;
import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;

import java.util.Map;

@UtilityClass
public class MinePositionFactory {

    public MinePosition fromBoundingBox(BoundingBox boundingBox, Map<XMaterial, MinePositionType> types) {

        final MinePosition position = new MinePosition();

        boundingBox.forEach(location -> {

            final Block block = location.getBlock();
            final XMaterial material = XMaterial.matchXMaterial(block.getType());

            if(material == XMaterial.AIR) {
                return;
            }

            final MinePositionType positionType = types.get(material);

            if(positionType == null) {
                return;
            }

            position.setPosition(positionType, location);

        });

        return position;
    }

}

package fr.robotv2.api.storage.dto;

import fr.robotv2.api.mine.PrivateMineState;
import fr.robotv2.api.position.MinePosition;
import fr.robotv2.api.vector.BoundingBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class PrivateMineDto {

    private final UUID minedId;

    private final UUID ownerId;

    private final String configurationName;

    private final MinePosition minePosition;

    private final BoundingBox entireZoneBox;

    private final BoundingBox mineAreaBox;

    private final HashSet<UUID> allowed;

    private final PrivateMineState state;

    private final int size;

    private final int level;

    private final String mineName;
}

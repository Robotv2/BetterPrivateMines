package fr.robotv2.adapter;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;
import fr.robotv2.api.worldedit.WorldEditAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LatestWorldEditAdapter extends WorldEditAdapter<XMaterial> {

    private final Map<File, ClipboardFormat> cachedClipBoardFormats = new HashMap<>();

    public LatestWorldEditAdapter(Function<MineMaterial, XMaterial> resolver) {
        super(resolver);
    }

    @Override
    public void fillRandom(BoundingBox boundingBox, PrivateMineConfiguration<?> configuration, CompletableFuture<Void> future) {

        final World world = Bukkit.getWorld(boundingBox.getWorldName());

        if(world == null) {
            throw new NullPointerException("world");
        }

        final RandomPattern randomPattern = new RandomPattern();
        double air = 1;

        for(Map.Entry<MineMaterial, Double> entry : configuration.getMaterials().entrySet()) {

            final XMaterial xMaterial = this.resolver.apply(entry.getKey());
            final BlockState block = BukkitAdapter.adapt(Objects.requireNonNull(xMaterial.parseMaterial()).createBlockData());

            double chance = entry.getValue() / 100;
            air -= chance;
            randomPattern.add(block, chance);
        }

        randomPattern.add(BukkitAdapter.adapt(Material.AIR.createBlockData()), air);

        try (final EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {

            BlockVector3 min = BlockVector3.at(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
            BlockVector3 max = BlockVector3.at(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
            Region region = new CuboidRegion(min, max);

            session.setBlocks(region, randomPattern);
            Operations.complete(session.commit());

            future.complete(null);

        } catch (WorldEditException exception) {
            exception.printStackTrace();
            future.completeExceptionally(exception);
        }
    }

    @Override
    public void fill(BoundingBox boundingBox, XMaterial xMaterial, CompletableFuture<Void> future) {

        final World world = Bukkit.getWorld(boundingBox.getWorldName());

        if (world == null) {
            throw new IllegalArgumentException("World not found: " + boundingBox.getWorldName());
        }

        final Material material = xMaterial.isSupported() ? Objects.requireNonNull(xMaterial.parseMaterial()) : Material.AIR;

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(world))) {

            BlockVector3 min = BlockVector3.at(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
            BlockVector3 max = BlockVector3.at(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());

            Region region = new CuboidRegion(min, max);
            editSession.setBlocks(region, BukkitAdapter.adapt(material.createBlockData()));

            Operations.complete(editSession.commit());
            future.complete(null);

        } catch (WorldEditException exception) {
            exception.printStackTrace();
            future.completeExceptionally(exception);
        }
    }

    @Override
    public void pasteSchematic(File file, Position vector, CompletableFuture<BoundingBox> future) {

        if(!file.exists()) {
            return;
        }

        try {
            ClipboardFormat format = cachedClipBoardFormats.getOrDefault(file, ClipboardFormats.findByFile(file));

            if (format == null) {
                future.completeExceptionally(new IllegalArgumentException("Unsupported or unknown clipboard format for file: " + file.getPath()));
                return;
            }

            try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
                Clipboard clipboard = reader.read();

                int width = clipboard.getDimensions().getX();
                int height = clipboard.getDimensions().getY();
                int length = clipboard.getDimensions().getZ();

                int newLength = length / 2;
                int newWidth = width / 2;
                int newHeight = height / 2;

                final Position position = vector.subtract(newWidth, newHeight, newLength); // Center the schematic
                final World world = Bukkit.getWorld(position.getWorldName());

                if(world == null) {
                    future.completeExceptionally(new NullPointerException("world"));
                    return;
                }

                clipboard.setOrigin(clipboard.getRegion().getMinimumPoint()); // Change the copy point to the minimum corner

                try (final EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(position.getX(), position.getY(), position.getZ()))
                            .ignoreAirBlocks(true)
                            .copyEntities(false)
                            .build();
                    Operations.complete(operation);
                    Operations.complete(editSession.commit());

                    cachedClipBoardFormats.putIfAbsent(file, format);

                    Position firstCorner = Position.of(position.getWorldName(), position.getX(), position.getY(), position.getZ());
                    Position secondCorner = Position.of(position.getWorldName(), position.getX() + width, position.getY() + height, position.getZ() + length);
                    BoundingBox boundingBox = new BoundingBox(firstCorner, secondCorner);

                    future.complete(boundingBox);
                }
            }
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }
    }
}

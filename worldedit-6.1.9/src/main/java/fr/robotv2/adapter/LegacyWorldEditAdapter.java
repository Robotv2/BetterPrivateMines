package fr.robotv2.adapter;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;
import fr.robotv2.api.worldedit.WorldEditAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LegacyWorldEditAdapter extends WorldEditAdapter<XMaterial> {

    private final Map<File, ClipboardFormat> cachedClipBoardFormats = new HashMap<>();

    public LegacyWorldEditAdapter(Function<MineMaterial, XMaterial> resolver) {
        super(resolver);
    }

    @Override
    public void fillRandom(BoundingBox boundingBox, PrivateMineConfiguration<?> configuration, CompletableFuture<Void> future) {

    }

    @Override
    public void fill(BoundingBox boundingBox, XMaterial xMaterial, CompletableFuture<Void> future) {

        final World world = Bukkit.getWorld(boundingBox.getWorldName());

        if (world == null) {
            future.completeExceptionally(new IllegalArgumentException("World not found: " + boundingBox.getWorldName()));
            return;
        }

        final BukkitWorld weWorld = new BukkitWorld(world);
        final EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);

        final BlockVector min = new BlockVector(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
        final BlockVector max = new BlockVector(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());

        Region region = new CuboidRegion(min, max);
        try {
            session.setBlocks(region, new BaseBlock(xMaterial.getId(), xMaterial.getData()));
            Operations.complete(session.commit());
            future.complete(null);
        } catch (WorldEditException exception) {
            exception.printStackTrace();
            future.completeExceptionally(exception);
        }
    }

    @Override
    public void pasteSchematic(File file, Position vector, CompletableFuture<BoundingBox> future) {

        if(!file.exists()) {
            future.completeExceptionally(new FileNotFoundException("schematic file"));
            return;
        }

        ClipboardFormat format = cachedClipBoardFormats.getOrDefault(file, ClipboardFormat.findByFile(file));

        if (format == null) {
            future.completeExceptionally(new IllegalArgumentException("Unsupported or unknown clipboard format for file: " + file.getPath()));
            return;
        }

        final org.bukkit.World world = Bukkit.getWorld(vector.getWorldName());

        if(world == null) {
            future.completeExceptionally(new NullPointerException("world"));
            return;
        }

        final BukkitWorld weWorld = new BukkitWorld(world);
        final WorldData worldData = weWorld.getWorldData();

        try {
            final ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
            final Clipboard clipboard = reader.read(worldData);

            int width = clipboard.getDimensions().getBlockX();
            int height = clipboard.getDimensions().getBlockY();
            int length = clipboard.getDimensions().getBlockZ();

            int newLength = length / 2;
            int newWidth = width / 2;
            int newHeight = height / 2;

            final Position position = vector.subtract(newWidth, newHeight, newLength); // Center the schematic
            clipboard.setOrigin(clipboard.getRegion().getMinimumPoint()); // Change the copy point to the minimum corner

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
            BlockVector to = new BlockVector(position.getX(), position.getY(), position.getZ());

            Operation operation = new ClipboardHolder(clipboard, worldData)
                    .createPaste(editSession, worldData)
                    .to(to)
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(operation);
            Operations.complete(editSession.commit());

            cachedClipBoardFormats.putIfAbsent(file, format);

            Position firstCorner = Position.of(position.getWorldName(), position.getX(), position.getY(), position.getZ());
            Position secondCorner = Position.of(position.getWorldName(), position.getX() + width, position.getY() + height, position.getZ() + length);
            BoundingBox boundingBox = new BoundingBox(firstCorner, secondCorner);

            future.complete(boundingBox);

        } catch (IOException | WorldEditException exception) {
            exception.printStackTrace();
            future.completeExceptionally(exception);
            return;
        }
    }
}
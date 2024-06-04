package fr.robotv2.adapter;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import fr.robotv2.api.material.MineMaterial;
import fr.robotv2.api.mine.PrivateMineConfiguration;
import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;
import fr.robotv2.api.worldedit.WorldEditAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LatestWorldEditAdapter extends WorldEditAdapter {

    private final Map<File, ClipboardFormat> cachedClipBoardFormats = new HashMap<>();

    @Override
    public <T> CompletableFuture<Void> fill(BoundingBox boundingBox, PrivateMineConfiguration<T> configuration) {

        final RandomPattern randomPattern = new RandomPattern();

        for(Map.Entry<MineMaterial, Double> entry : configuration.getMaterials().entrySet()) {
            try {
                final BaseBlock block = WorldEdit.getInstance().getBlockFactory().parseFromInput(entry.getKey().worldEditLiteral(), new ParserContext());
                randomPattern.add(block, entry.getValue() / 100);
            } catch (InputParseException e) {
                throw new RuntimeException(e);
            }
        }

        final World world = Bukkit.getWorld(boundingBox.getWorldName());

        if(world == null) {
            throw new NullPointerException("world");
        }

        return null;
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

                int width = clipboard.getDimensions().x();
                int height = clipboard.getDimensions().y();
                int length = clipboard.getDimensions().z();

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

                try (EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
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

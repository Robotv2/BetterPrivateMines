package fr.robotv2.adapter;

import fr.robotv2.api.vector.BoundingBox;
import fr.robotv2.api.vector.Position;
import fr.robotv2.api.worldedit.WorldEditAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class LatestWorldEditAdapter extends WorldEditAdapter {

    @Override
    public CompletableFuture<Void> fill(BoundingBox boundingBox) {
        return null;
    }

    @Override
    public void pasteSchematic(File file, Position vector, CompletableFuture<BoundingBox> future) {

        if(!file.exists()) {
            InfDungeon.logger().warning("This filed doesn't exist.");
            return;
        }

        try {
            ClipboardFormat format = cachedClipboardFormat.getOrDefault(file, ClipboardFormats.findByFile(file));

            if (format == null) {
                InfDungeon.logger().warning("Unsupported or unknown clipboard format for file: " + file.getPath());
                future.completeExceptionally(new IllegalArgumentException("Unsupported or unknown clipboard format for file: " + file.getPath()));
                return;
            }

            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();

                int width = clipboard.getDimensions().getBlockX();
                int height = clipboard.getDimensions().getBlockY();
                int length = clipboard.getDimensions().getBlockZ();

                int newLength = length / 2;
                int newWidth = width / 2;
                int newHeight = height / 2;

                location.subtract(newWidth, newHeight, newLength); // Center the schematic

                clipboard.setOrigin(clipboard.getRegion().getMinimumPoint()); // Change the copy point to the minimum corner

                try (EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(new BukkitWorld(location.getWorld()))) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                            .ignoreAirBlocks(true)
                            .copyEntities(false)
                            .build();
                    Operations.complete(operation);
                    Operations.complete(editSession.commit());

                    cachedClipboardFormat.putIfAbsent(file, format);

                    Location firstCorner = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
                    Location secondCorner = new Location(location.getWorld(), location.getX() + width, location.getY() + height, location.getZ() + length);
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

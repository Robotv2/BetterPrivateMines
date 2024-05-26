package fr.robotv2.betterprivatemines.schematic;

import fr.robotv2.betterprivatemines.BetterPrivateMines;
import fr.robotv2.betterprivatemines.schematic.placement.GridPosition;
import fr.robotv2.betterprivatemines.schematic.placement.SchematicPlacement;
import fr.robotv2.betterprivatemines.schematic.placement.impl.HelixPositionResolver;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SchematicManager {

    private final BetterPrivateMines plugin;
    private final File schematicFolder;
    private final Map<String, File> files;

    private final File worldStateFile;
    @Getter private final SchematicPlacement schematicPlacement;

    public SchematicManager(final BetterPrivateMines plugin) {
        this.plugin = plugin;

        this.schematicFolder = new File(plugin.getDataFolder(), "schematic");
        if(!schematicFolder.exists()) schematicFolder.mkdir();

        this.files = new HashMap<>();

        this.worldStateFile = new File(plugin.getDataFolder(), "world_state.json");
        this.schematicPlacement = loadSchematicPlacement();
    }

    public void loadSchematics() {

        files.clear();

        if(schematicFolder == null || !schematicFolder.exists()) {
            return;
        }

        final File[] listFiles = schematicFolder.listFiles();

        if(listFiles == null) {
            return;
        }

        for(File file : listFiles) {
            files.put(file.getName(), file);
        }
    }

    public MineSchematic getMineSchematic(String schematicName) {
        for (Map.Entry<String, File> entry : files.entrySet()) {

            String fileNameWithoutExtension = entry.getKey().substring(0, entry.getKey().lastIndexOf('.'));

            if (schematicName.equals(entry.getKey()) || fileNameWithoutExtension.equals(schematicName)) {
                return new MineSchematic(entry.getValue());
            }
        }

        return null;
    }

    public void saveSchematicPlacement() {

        BetterPrivateMines.logger().info("Saving world state file...");

        try (final Writer writer = new BufferedWriter(new FileWriter(worldStateFile))) {
            BetterPrivateMines.gson().toJson(schematicPlacement, SchematicPlacement.class, writer);
            BetterPrivateMines.logger().info("Saving world state file successfully.");
        } catch (IOException exception) {
            BetterPrivateMines.logger().log(Level.SEVERE, "An error occurred while saving world state file.", exception);
        }
    }

    private SchematicPlacement loadSchematicPlacement() {

        try {

            if(!worldStateFile.exists()) {
                worldStateFile.createNewFile();
                return new SchematicPlacement(new GridPosition(0, 0), new HelixPositionResolver());
            }

            try (final Reader reader = new BufferedReader(new FileReader(worldStateFile))) {
                return BetterPrivateMines.gson().fromJson(reader, SchematicPlacement.class);
            }

        } catch (IOException exception) {
            BetterPrivateMines.logger().log(Level.SEVERE, "An error occurred while reading world state file.", exception);
            return new SchematicPlacement(new GridPosition(0, 0), new HelixPositionResolver());
        }
    }
}

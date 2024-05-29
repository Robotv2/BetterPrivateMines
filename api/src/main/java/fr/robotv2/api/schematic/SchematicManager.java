package fr.robotv2.api.schematic;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchematicManager {

    private final File schematicFolder;
    private final Map<String, File> files;

    public SchematicManager(File schematicFolder) {
        this.schematicFolder = schematicFolder;
        this.files = new HashMap<>();

        if(!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }
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

    @Nullable
    public File getSchematic(String schematicName) {
        for (Map.Entry<String, File> entry : files.entrySet()) {

            String fileNameWithoutExtension = entry.getKey().substring(0, entry.getKey().lastIndexOf('.'));

            if (schematicName.equals(entry.getKey()) || fileNameWithoutExtension.equals(schematicName)) {
                return entry.getValue();
            }
        }

        return null;
    }
}

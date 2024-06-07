package fr.robotv2.api.grid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.logging.Level;

@Getter
@Log
public class GridManager {

    private final Gson gson;

    private final Grid grid;
    private final File worldStateFile;

    public GridManager(File worldStateFile) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(GridPositionResolver.class, new GridJsonAdapter())
                .create();
        this.worldStateFile = worldStateFile;
        this.grid = loadGridFromFile(worldStateFile);
    }

    private Grid loadGridFromFile(File file) {

        try {

            if(!file.isFile()) {
                file.createNewFile();
                return Grid.createDefault();
            }

            return gson.fromJson(Files.newBufferedReader(file.toPath()), Grid.class);

        } catch (IOException exception) {
            log.log(Level.SEVERE, "An error occurred while reading grid file", exception);
            return Grid.createDefault();
        }
    }

    public void saveGrid() {
        try(Writer writer = Files.newBufferedWriter(getWorldStateFile().toPath())) {
            gson.toJson(getGrid(), writer);
        } catch (IOException exception) {
            log.log(Level.SEVERE, "An error occurred while saving grid file", exception);
        }
    }
}

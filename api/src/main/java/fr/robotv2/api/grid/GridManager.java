package fr.robotv2.api.grid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.robotv2.api.grid.adapter.GridPositionResolverJsonAdapter;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.*;
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
                .registerTypeAdapter(GridPositionResolver.class, new GridPositionResolverJsonAdapter())
                .create();
        this.worldStateFile = worldStateFile;
        this.grid = loadGridFromFile(worldStateFile);
    }

    private Grid loadGridFromFile(File file) {

        Grid grid = Grid.createDefault();

        try {

            if(file.isFile()) {

                try (final Reader reader = new BufferedReader(new FileReader(file))) {
                    grid = gson.fromJson(reader, Grid.class);
                }

            }

        } catch (IOException exception) {
            log.log(Level.SEVERE, "An error occurred while reading grid file", exception);
        }

        return grid;
    }

    public void saveGrid() {
        try(Writer writer = Files.newBufferedWriter(getWorldStateFile().toPath())) {
            gson.toJson(getGrid(), writer);
        } catch (IOException exception) {
            log.log(Level.SEVERE, "An error occurred while saving grid file", exception);
        }
    }
}

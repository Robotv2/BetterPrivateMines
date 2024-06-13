package fr.robotv2.api.storage.impl;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.MigrationManager;
import fr.robotv2.api.mine.PrivateMine;
import fr.robotv2.api.storage.CompletableStorage;
import fr.robotv2.api.storage.TableUtils;
import fr.robotv2.api.storage.dto.PrivateMineDto;
import fr.robotv2.api.storage.migration.PrivateMineTableMigration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SqlStorage extends TableUtils implements CompletableStorage<UUID, PrivateMine> {

    private final static Logger SQL_LOGGER = Logger.getLogger("SQL");
    public final static String PRIVATE_MINE_TABLE = "betterprivatemines_mines";

    public SqlStorage(DatabaseConnection connection) {
        super(SQL_LOGGER, connection);
        setupMigrations();
    }

    private void setupMigrations() {
        MigrationManager.registerMigration(new PrivateMineTableMigration());
        MigrationManager.execute(connection.getConnection(), connection.getDatabaseConfiguration(), SQL_LOGGER);
    }

    @Override
    public CompletableFuture<Optional<PrivateMine>> select(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final List<PrivateMineDto> dtos = requestHelper.select(PRIVATE_MINE_TABLE, PrivateMineDto.class, (table) -> {
                table.uuid("mineId", uuid);
            });

            if(dtos.isEmpty()) {
                return Optional.empty();
            }

            if(dtos.size() > 1) {
                SQL_LOGGER.log(Level.SEVERE, "Multiple records were find for id " + uuid + ". THIS IS NOT SUPPOSED TO HAPPEN.");
            }

            return Optional.of(new PrivateMine(dtos.get(0)));
        });
    }

    @Override
    public CompletableFuture<List<PrivateMine>> selectAll() {
        return CompletableFuture.supplyAsync(() -> {
            return requestHelper.selectAll(PRIVATE_MINE_TABLE, PrivateMineDto.class).
                    stream()
                    .filter(Objects::nonNull)
                    .map(PrivateMine::new)
                    .collect(Collectors.toList())
                    ;
        });
    }

    @Override
    public CompletableFuture<PrivateMine> insert(PrivateMine value) {
        return CompletableFuture.supplyAsync(() -> {

            requestHelper.insert(PRIVATE_MINE_TABLE, (table) -> {
                table.uuid("mineId", value.getMineId());
                table.uuid("ownerId", value.getOwnerId());
                table.string("configurationName", value.getConfigurationName());
                table.blob("minePosition", value.getMinePosition());
                table.blob("entireZoneBox", value.getEntireZoneBox());
                table.blob("mineAreaBox", value.getMineAreaBox());
                table.blob("allowed", value.getAllowed());
                table.string("state", value.getState().name());
                table.bigInt("size", value.getSize());
                table.bigInt("level", value.getLevel());
                table.string("mineName", value.getMineName()).nullable();
            });

            return value;
        });
    }

    @Override
    public CompletableFuture<PrivateMine> update(UUID uuid, PrivateMine value) {
        return CompletableFuture.supplyAsync(() -> {

            requestHelper.upsert(PRIVATE_MINE_TABLE, (table) -> {
                table.uuid("mineId", value.getMineId());
                table.uuid("ownerId", value.getOwnerId());
                table.string("configurationName", value.getConfigurationName());
                table.blob("minePosition", value.getMinePosition());
                table.blob("entireZoneBox", value.getEntireZoneBox());
                table.blob("mineAreaBox", value.getMineAreaBox());
                table.blob("allowed", value.getAllowed());
                table.string("state", value.getState().name());
                table.bigInt("size", value.getSize());
                table.bigInt("level", value.getLevel());
                table.string("mineName", value.getMineName()).nullable();
            });

            return value;
        });
    }

    @Override
    public CompletableFuture<Void> remove(PrivateMine value) {
        return CompletableFuture.runAsync(() -> {
            requestHelper.delete(PRIVATE_MINE_TABLE, (table) -> {
                table.uuid("mineId", value.getMineId());
            });
        });
    }

    @Override
    public CompletableFuture<Void> removeFromId(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            requestHelper.delete(PRIVATE_MINE_TABLE, (table) -> {
                table.uuid("mineId", uuid);
            });
        });
    }

    @Override
    public void close() {
        connection.disconnect();
    }
}

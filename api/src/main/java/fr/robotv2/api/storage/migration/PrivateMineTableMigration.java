package fr.robotv2.api.storage.migration;

import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Migration;
import fr.robotv2.api.storage.impl.SqlStorage;

public class PrivateMineTableMigration extends Migration {

    @Override
    public void up() {
        SchemaBuilder.create(this, SqlStorage.PRIVATE_MINE_TABLE, (table) -> {
            table.uuid("mineId").primary();
            table.uuid("ownerId");
            table.string("configurationName", 32);
            table.blob("minePosition");
            table.blob("entireZoneBox");
            table.blob("mineAreaBox");
            table.blob("allowed");
            table.string("state", 16);
            table.bigInt("size").defaultValue("1");
            table.bigInt("level").defaultValue("1");
            table.string("mineName", 64).nullable();
        });
    }
}

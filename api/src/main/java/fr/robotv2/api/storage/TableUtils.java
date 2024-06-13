package fr.robotv2.api.storage;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.RequestHelper;

import java.util.logging.Logger;

public abstract class TableUtils {

    protected final Logger logger;
    protected final DatabaseConnection connection;
    protected final RequestHelper requestHelper;

    protected TableUtils(Logger logger, DatabaseConnection connection) {
        this.logger = logger;
        this.connection = connection;
        this.requestHelper = new RequestHelper(connection, logger);
    }
}

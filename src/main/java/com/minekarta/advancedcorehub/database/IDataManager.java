package com.minekarta.advancedcorehub.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for the DataManager, defining the contract for database operations.
 */
public interface IDataManager {

    /**
     * Initializes the database, setting up the connection pool and creating tables.
     */
    void initDatabase();

    /**
     * Gets a connection from the connection pool.
     *
     * @return A database {@link Connection}.
     * @throws SQLException if a database access error occurs.
     */
    Connection getConnection() throws SQLException;

    /**
     * Closes the data source and releases all resources.
     */
    void closeDataSource();
}
package Com.base.FunctionLibarary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DBkeywords - multi-database JDBC keyword library.
 * Holds the "current" connection / statement / result set so step definitions
 * can chain operations without passing handles around.
 */
public class DBkeywords {

    private static final Logger log = LogManager.getLogger(DBkeywords.class);

    private static Connection currentConnection;
    private static Statement  currentStatement;
    private static ResultSet  currentResultSet;

    // ===================================================================
    //  CURRENT STATE GETTERS / SETTERS
    // ===================================================================

    public static Connection getCurrentDbConnection() { return currentConnection; }
    public static void setCurrentDbConnection(Connection conn) { currentConnection = conn; }

    public static Statement getCurrentDbStatement() { return currentStatement; }
    public static void setCurrentDbStatement(Statement stmt) { currentStatement = stmt; }

    public static ResultSet getCurrentResultSet() { return currentResultSet; }
    public static void setCurrentResultSet(ResultSet rs) { currentResultSet = rs; }

    // ===================================================================
    //  CONNECTION FACTORIES (one per DB vendor)
    // ===================================================================

    // generic helper used by all the vendor methods
    private static Connection connect(String driverClass, String url, String user, String password) {
        try {
            Class.forName(driverClass);
            Connection conn = DriverManager.getConnection(url, user, password);
            currentConnection = conn;
            log.info("Connected: " + url);
            return conn;
        } catch (Exception e) {
            log.error("DB connection failed (" + url + "): " + e.getMessage());
            return null;
        }
    }

    public static Connection createOracleDatabaseConnection(String host, int port, String service,
                                                            String user, String password) {
        String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + service;
        return connect("oracle.jdbc.driver.OracleDriver", url, user, password);
    }

    public static Connection createPostgresSQLConnection(String host, int port, String database,
                                                         String user, String password) {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        return connect("org.postgresql.Driver", url, user, password);
    }

    // set the active schema / search_path on a Postgres connection
    public static void setupPostgresSQLSchemaSetup(String schema) {
        try {
            Statement stmt = currentConnection.createStatement();
            stmt.execute("SET search_path TO " + schema);
            stmt.close();
            log.info("Postgres search_path set to: " + schema);
        } catch (Exception e) {
            log.error("setupPostgresSQLSchemaSetup failed: " + e.getMessage());
        }
    }

    public static Connection createDb2DatabaseConnection(String host, int port, String database,
                                                         String user, String password) {
        String url = "jdbc:db2://" + host + ":" + port + "/" + database;
        return connect("com.ibm.db2.jcc.DB2Driver", url, user, password);
    }

    public static Connection createAs400DatabaseConnection(String host, String user, String password) {
        String url = "jdbc:as400://" + host;
        return connect("com.ibm.as400.access.AS400JDBCDriver", url, user, password);
    }

    public static Connection createRedshiftDatabaseConnection(String host, int port, String database,
                                                              String user, String password) {
        String url = "jdbc:redshift://" + host + ":" + port + "/" + database;
        return connect("com.amazon.redshift.jdbc.Driver", url, user, password);
    }

    public static Connection createMysqlDatabaseConnection(String host, int port, String database,
                                                           String user, String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        return connect("com.mysql.cj.jdbc.Driver", url, user, password);
    }

    public static Connection createSybaseConnection(String host, int port, String database,
                                                    String user, String password) {
        String url = "jdbc:sybase:Tds:" + host + ":" + port + "/" + database;
        return connect("com.sybase.jdbc4.jdbc.SybDriver", url, user, password);
    }

    // ===================================================================
    //  STATEMENTS
    // ===================================================================

    // create a plain statement on the current connection
    public static Statement createStatement() {
        try {
            currentStatement = currentConnection.createStatement();
            return currentStatement;
        } catch (Exception e) {
            log.error("createStatement failed: " + e.getMessage());
            return null;
        }
    }

    // create a statement and set the schema first (vendor-dependent)
    public static Statement createStatementWithSchema(String schema) {
        try {
            currentStatement = currentConnection.createStatement();
            currentConnection.setSchema(schema);   // JDBC 4.1+
            log.info("Statement created with schema: " + schema);
            return currentStatement;
        } catch (Exception e) {
            log.error("createStatementWithSchema failed: " + e.getMessage());
            return null;
        }
    }

    // ===================================================================
    //  EXECUTE
    // ===================================================================

    // run a SELECT query, stores + returns the ResultSet
    public static ResultSet executeQuery(String sql) {
        try {
            if (currentStatement == null) createStatement();
            currentResultSet = currentStatement.executeQuery(sql);
            log.info("Executed query: " + sql);
            return currentResultSet;
        } catch (Exception e) {
            log.error("executeQuery failed: " + e.getMessage());
            return null;
        }
    }

    // ===================================================================
    //  READ RESULTS
    // ===================================================================

    // return the FIRST row of the current result set as column->value map
    public static Map<String, String> getValueAsDictionary() {
        Map<String, String> row = new LinkedHashMap<>();
        try {
            if (currentResultSet != null && currentResultSet.next()) {
                ResultSetMetaData meta = currentResultSet.getMetaData();
                int cols = meta.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnName(i), currentResultSet.getString(i));
                }
            }
        } catch (Exception e) {
            log.error("getValueAsDictionary failed: " + e.getMessage());
        }
        return row;
    }

    // return ALL rows as a list of column->value maps
    public static List<Map<String, String>> getAllRowsAsDictionary() {
        List<Map<String, String>> rows = new ArrayList<>();
        try {
            if (currentResultSet != null) {
                ResultSetMetaData meta = currentResultSet.getMetaData();
                int cols = meta.getColumnCount();
                while (currentResultSet.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 1; i <= cols; i++) {
                        row.put(meta.getColumnName(i), currentResultSet.getString(i));
                    }
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            log.error("getAllRowsAsDictionary failed: " + e.getMessage());
        }
        return rows;
    }

    // ===================================================================
    //  CLEANUP
    // ===================================================================

    public static void closeDbConnection() {
        try {
            if (currentResultSet != null) currentResultSet.close();
            if (currentStatement != null) currentStatement.close();
            if (currentConnection != null) currentConnection.close();
            log.info("DB connection closed");
        } catch (Exception e) {
            log.error("closeDbConnection failed: " + e.getMessage());
        }
    }
}
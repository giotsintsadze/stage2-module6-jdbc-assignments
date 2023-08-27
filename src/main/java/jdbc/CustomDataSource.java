package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    
    private static final Object MONITOR = new Object();
    private static CustomDataSource instance;
    private static final SQLException SQL_EXCEPTION = new SQLException();

    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            synchronized (MONITOR) {
                if (instance == null) {
                    instance = initializeInstance();
                }
            }
        }
        return instance;
    }

    private static CustomDataSource initializeInstance() {
        try {
            Properties properties = new Properties();
            properties.load(
                    CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties")
            );

            String driver = properties.getProperty("postgres.driver");
            String url = properties.getProperty("postgres.url");
            String password = properties.getProperty("postgres.password");
            String name = properties.getProperty("postgres.name");

            return new CustomDataSource(driver, url, password, name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
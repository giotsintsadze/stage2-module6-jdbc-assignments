package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    instance = new CustomDataSource(
                            "org.postgresql.Driver",
                            "jdbc:mysql://localhost:3306/myfirstdb",
                            "password",
                            "postgres"
                    );
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(this.url, this.name, this.password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, name, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new SQLException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw  new SQLException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
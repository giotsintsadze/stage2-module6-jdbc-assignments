package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleJDBCRepository {
    private CustomDataSource dataSource;

    public SimpleJDBCRepository(CustomDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long createUser(User user) {
        String createUserSQL = "INSERT INTO users (first_name, last_name, age) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    public User findUserByName(String userName) {
        String findUserByNameSQL = "SELECT id, first_name, last_name, age FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {

            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return User.builder()
                            .id(rs.getLong("id"))
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .age(rs.getInt("age"))
                            .build();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by name", e);
        }
    }

    public List<User> findAllUser() {
        String findAllUserSQL = "SELECT id, first_name, last_name, age FROM users";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(findAllUserSQL);
             ResultSet rs = ps.executeQuery()) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(User.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .age(rs.getInt("age"))
                        .build());
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
    }

    public User updateUser(User user) {
        String updateUserSQL = "UPDATE users SET first_name = ?, last_name = ?, age = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

}

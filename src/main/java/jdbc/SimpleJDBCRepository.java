package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private interface StatementFunction<S, R> {
        R apply(S statement) throws SQLException;
    }

    private final CustomDataSource dataSource = CustomDataSource.getInstance();

    private static final String createUserSQL = "INSERT INTO myusers(firstname, lastname, age) VALUES (?, ?, ?);";
    private static final String updateUserSQL = "UPDATE myusers SET firstname=?, lastname=?, age=? WHERE id=?;";
    private static final String deleteUserSQL = "DELETE FROM public.myusers WHERE id=?;";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id=?;";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname=?;";
    private static final String findAllUserSQL = "SELECT * FROM myusers;";

    private <T> T query(String sqlForStatement,
                        StatementFunction<PreparedStatement, T> statementToResult,
                        int... additionalConstants) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sqlForStatement, additionalConstants)) {
            return statementToResult.apply(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long createUser(User user) {
        return query(createUserSQL, statement -> {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.execute();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            return generatedKeys.next() ? generatedKeys.getLong(1) : null;
        }, PreparedStatement.RETURN_GENERATED_KEYS);
    }

    public User findUserById(Long userId) {
        return query(findUserByIdSQL, statement -> {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        });
    }

    public User findUserByName(String userName) {
        return query(findUserByNameSQL, statement -> {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        });
    }

    public List<User> findAllUser() {
        return query(findAllUserSQL, statement -> {
            List<User> users = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
            return users;
        });
    }

    public User updateUser(User user) {
        return query(updateUserSQL, statement -> {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.setLong(4, user.getId());
            return statement.executeUpdate() != 0 ? findUserById(user.getId()) : null;
        });
    }

    public void deleteUser(Long userId) {
        query(deleteUserSQL, statement -> {
            statement.setLong(1, userId);
            statement.executeUpdate();
            return null;
        });
    }

    private User map(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .age(rs.getInt("age"))
                .build();
    }
}

package server.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.User;
import server.DBConnection;

/**
 * Data Access Object (DAO) for handling database operations related to login.
 * This completely separates database querying from the business logic.
 */
public class LoginDAO {
    
    /**
     * Retrieves a user from the database matching the provided credentials.
     * 
     * @param email The user's email
     * @param password The user's password
     * @return A User object if found, otherwise null
     */
    public User getUserByCredentials(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE email=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("ic_passport_number"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

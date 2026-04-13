package server.classes;

import java.util.regex.Pattern;
import model.User;

/**
 * Handles business logic for authentication.
 * It uses the LoginDAO to handle raw database interactions, adhering to SRP.
 */
public class LoginClass {
    
    private static final LoginDAO loginDAO = new LoginDAO();
    
    // Strict regular expression for basic email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    /**
     * Authenticates a user, performing business validations before querying the database.
     * @param email The user's email
     * @param password The user's password
     * @return User object if authenticated, null otherwise
     */
    public static User authenticate(String email, String password) {
        // Business logic layer: prevent empty strings
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("Login Failed: Missing email or password.");
            return null;
        }
        
        // Clean and validate email format (Defense in Depth)
        String cleanEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            System.err.println("Login Failed: Invalid email format detected.");
            return null;
        }

        // Delegate data fetching to DAO
        return loginDAO.getUserByCredentials(cleanEmail, password);
    }
}

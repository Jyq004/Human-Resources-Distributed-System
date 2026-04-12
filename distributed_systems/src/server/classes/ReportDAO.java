package server.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.DBConnection;

/**
 * Data Access Object (DAO) for handling database combinations required for reports.
 * Used by YearlyReportClass to generate Yearly Leave Reports.
 */
public class ReportDAO {

    /**
     * Retrieves leave application data for a specific user and year.
     * @param userId The ID of the user.
     * @param year The year to filter by.
     * @return Formatted string containing the report data.
     */
    public String fetchIndividualLeaveData(int userId, int year) {
        StringBuilder data = new StringBuilder();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.first_name, u.last_name, la.leave_type, la.start_date, la.end_date, la.status " +
                         "FROM leave_application la " +
                         "JOIN user u ON la.user_id = u.user_id " +
                         "WHERE la.user_id = ? AND YEAR(la.start_date) = ? " +
                         "ORDER BY la.start_date ASC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, year);
            
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                if (count == 0) {
                    data.append("Employee: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                    data.append("--------------------------------------------------\n");
                }
                data.append(String.format("- %s : %s to %s [%s]\n", 
                        rs.getString("leave_type"),
                        rs.getDate("start_date").toString(),
                        rs.getDate("end_date").toString(),
                        rs.getString("status")));
                count++;
            }
            
            if (count == 0) {
                data.append("No leave records found for this user in ").append(year).append(".\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving individual report data from database.";
        }
        
        return data.toString();
    }

    /**
     * Retrieves leave application data for all users in a specific year.
     * @param year The year to filter by.
     * @return Formatted string containing the company-wide report data.
     */
    public String fetchCompanyLeaveData(int year) {
        StringBuilder data = new StringBuilder();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.user_id, u.first_name, u.last_name, la.leave_type, la.start_date, la.end_date, la.status " +
                         "FROM leave_application la " +
                         "JOIN user u ON la.user_id = u.user_id " +
                         "WHERE YEAR(la.start_date) = ? " +
                         "ORDER BY u.user_id ASC, la.start_date ASC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, year);
            
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                data.append(String.format("ID: %d | %s %s | %s | %s to %s | [%s]\n", 
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date").toString(),
                        rs.getDate("end_date").toString(),
                        rs.getString("status")));
                count++;
            }
            
            if (count == 0) {
                data.append("No leave records found company-wide in ").append(year).append(".\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving company report data from database.";
        }
        
        return data.toString();
    }
}

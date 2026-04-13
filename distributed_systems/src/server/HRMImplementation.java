/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import shared.HRMInterface;
import model.*;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HRMImplementation extends UnicastRemoteObject implements HRMInterface {

    public HRMImplementation() throws RemoteException {
        super();
    }

    // 🔐 LOGIN
    @Override
    public User login(String email, String password) throws RemoteException {

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

    // 📊 CHECK BALANCE
    @Override
    public int checkLeaveBalance(int userId) throws RemoteException {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT total_leave - used_leave AS balance FROM leave_balance WHERE user_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("balance");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🏖️ APPLY LEAVE
    @Override
    public String applyLeave(LeaveApplication leave) throws RemoteException {

        int days = (int) ChronoUnit.DAYS.between(
                leave.getStartDate(), leave.getEndDate()) + 1;

        try (Connection conn = DBConnection.getConnection()) {

            String checkSql = "SELECT total_leave, used_leave FROM leave_balance WHERE user_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, leave.getUserId());

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total_leave");
                int used = rs.getInt("used_leave");

                if (total - used < days) {
                    return "Not enough leave";
                }

                // Insert leave
                String insertSql = "INSERT INTO leave_application(user_id, leave_type, start_date, end_date, reason, status, applied_date) VALUES (?, ?, ?, ?, ?, 'Pending', CURDATE())";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);

                insertStmt.setInt(1, leave.getUserId());
                insertStmt.setString(2, leave.getLeaveType());
                insertStmt.setDate(3, java.sql.Date.valueOf(leave.getStartDate()));
                insertStmt.setDate(4, java.sql.Date.valueOf(leave.getEndDate()));
                insertStmt.setString(5, leave.getReason());

                insertStmt.executeUpdate();

                return "Leave applied (Pending approval)";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No record";
    }

    // 🧑‍💼 HR VIEW ALL
    @Override
    public List<LeaveApplication> getAllLeaves() throws RemoteException {

        List<LeaveApplication> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM leave_application WHERE status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new LeaveApplication(
                        rs.getInt("leave_id"),
                        rs.getInt("user_id"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getString("reason")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ✅ APPROVE / REJECT
    @Override
    public String updateLeaveStatus(int leaveId, String status) throws RemoteException {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "UPDATE leave_application SET status=? WHERE leave_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, status);
            stmt.setInt(2, leaveId);

            stmt.executeUpdate();

            return "✅ Updated to " + status;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "❌ Failed";
    }

    public User getUser(int userId) throws RemoteException {

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    } 
    
    public String registerUser(User user) throws RemoteException {

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO user(first_name,last_name,ic_passport_number,email,password,role) VALUES (?,?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getIc());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int userId = 0;

            if (rs.next()) {
                userId = rs.getInt(1);
            }

            String leaveSql = "INSERT INTO leave_balance (user_id, total_leave, used_leave) VALUES (?, ?, ?)";

            PreparedStatement leavePs = conn.prepareStatement(leaveSql);
            leavePs.setInt(1, userId);
            leavePs.setInt(2, 14);
            leavePs.setInt(3, 0);

            leavePs.executeUpdate();

            return "User Registered Successfully";

        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
    public String updateUser(User user) throws RemoteException {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE user SET first_name=?, last_name=?, ic_passport_number=?, email=? WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getIc());
            ps.setString(4, user.getEmail());
            ps.setInt(5, user.getUserId());

            ps.executeUpdate();

            
            return "Profile Updated Successfully";

        } catch (Exception e) {
            return e.getMessage();
        }
    }
        
    @Override
    public PersonalDetail getPersonalDetailByUserId(int userId) throws RemoteException {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM personaldetail WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new PersonalDetail(
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("date_of_birth"),
                        rs.getString("contact_name"),
                        rs.getString("relationship"),
                        rs.getString("contact_number")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public String updatePersonalDetail(PersonalDetail detail) throws RemoteException {
        try {
            Connection conn = DBConnection.getConnection();

            String checkSql = "SELECT * FROM personaldetail WHERE user_id=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, detail.getUserId());

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE personaldetail SET phone_number=?, address=?, date_of_birth=?, contact_name=?, relationship=?, contact_number=? WHERE user_id=?";
                PreparedStatement ps = conn.prepareStatement(updateSql);

                ps.setString(1, detail.getPhoneNumber());
                ps.setString(2, detail.getAddress());
                ps.setString(3, detail.getDateOfBirth());
                ps.setString(4, detail.getContactName());
                ps.setString(5, detail.getRelationship());
                ps.setString(6, detail.getContactNumber());
                ps.setInt(7, detail.getUserId());

                ps.executeUpdate();

                return "Personal Detail Updated Successfully";

            } else {
                String insertSql = "INSERT INTO personaldetail(user_id, phone_number, address, date_of_birth, contact_name, relationship, contact_number) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(insertSql);

                ps.setInt(1, detail.getUserId());
                ps.setString(2, detail.getPhoneNumber());
                ps.setString(3, detail.getAddress());
                ps.setString(4, detail.getDateOfBirth());
                ps.setString(5, detail.getContactName());
                ps.setString(6, detail.getRelationship());
                ps.setString(7, detail.getContactNumber());

                ps.executeUpdate();

                return "Personal Detail Added Successfully";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
    
      public String changePassword(int userId, String oldPass, String newPass) throws RemoteException {

        try {
            Connection conn = DBConnection.getConnection();

            String checkSql = "SELECT password FROM user WHERE user_id=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, userId);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                return "User Not Found";
            }

            String dbPassword = rs.getString("password");

            if (!dbPassword.equals(oldPass)) {
                return "Old Password Incorrect";
            }

            String updateSql = "UPDATE user SET password=? WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(updateSql);

            ps.setString(1, newPass);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            return rows > 0 ? "Password Changed Successfully" : "Update Failed";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // VIEW LEAVE HISTORY AND VIEW LEAVE STATUS
        @Override
    public List<LeaveApplication> getLeaveHistory(int userId, String status) throws RemoteException {

        List<LeaveApplication> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String sql;
            PreparedStatement stmt;

            if (status.equalsIgnoreCase("HISTORY")) {

                sql = "SELECT * FROM leave_application WHERE user_id=? AND status IN ('Approved','Rejected')";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);

            }

            else if (status.equalsIgnoreCase("ALL")) {

                sql = "SELECT * FROM leave_application WHERE user_id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);

            }

            else {

                sql = "SELECT * FROM leave_application WHERE user_id=? AND status=?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setString(2, status);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new LeaveApplication(
                     // rs.getInt("leave_id"),
                        rs.getInt("user_id"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getString("reason"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
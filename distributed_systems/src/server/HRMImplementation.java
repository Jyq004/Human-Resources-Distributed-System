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
                    return "❌ Not enough leave";
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

                return "✅ Leave applied (Pending approval)";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";
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
    
}
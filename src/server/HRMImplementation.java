/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import model.*;
import server.classes.*;
import shared.HRMInterface;

public class HRMImplementation extends UnicastRemoteObject implements HRMInterface {

    public HRMImplementation() throws RemoteException {
        super();
    }

    // 🔐 LOGIN
    @Override
    public User login(String email, String password) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
                // Delegated the actual DB logic to LoginClass to improve OOP consistency
                return LoginClass.authenticate(email, password);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Login task execution failed", e);
        }
    }

    // 📊 CHECK BALANCE
    @Override
    public int checkLeaveBalance(int userId) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
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
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Check leave task execution failed", e);
        }
    }

    // 🏖️ APPLY LEAVE
    @Override
    public String applyLeave(LeaveApplication leave) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
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
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Apply leave task execution failed", e);
        }
    }

    // 🧑‍💼 HR VIEW ALL
    @Override
    public List<LeaveApplication> getAllLeaves() throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
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
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Get all leaves task execution failed", e);
        }
    }

    // ✅ APPROVE / REJECT
    @Override
    public String updateLeaveStatus(int leaveId, String status) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
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
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Update leave status task execution failed", e);
        }
    }

    public User getUser(int userId) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
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
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Get user task execution failed", e);
        }
    } 
    
    // (RYAN) Yearly Leave Report Generation
    @Override
    public String generateIndividualReport(int userId, int year) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
                // Offloads to the business class which handles the layout and DAO
                return YearlyReportClass.generateIndividualReport(userId, year);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Individual Report task execution failed", e);
        }
    }

    @Override
    public String generateCompanyReport(int year) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
                // Offloads to the business class which handles the layout and DAO
                return YearlyReportClass.generateCompanyReport(year);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Company Report task execution failed", e);
        }
    }
}
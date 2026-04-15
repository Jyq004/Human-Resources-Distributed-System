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
            return Multithread.executeTask("Login Authentication for " + email, () -> {
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
            return Multithread.executeTask("Check Leave Balance for User " + userId, () -> {
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
            return Multithread.executeTask("Apply Leave for User " + leave.getUserId(), () -> {
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
            return Multithread.executeTask("Get All Pending Leaves", () -> {
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

    // (HR) APPROVE / REJECT LEAVE 
    @Override
    public String updateLeaveStatus(int leaveId, String status) throws RemoteException {
        try {
            return Multithread.executeTask(() -> {
                try (Connection conn = DBConnection.getConnection()) {

                    // Get leave details
                    String selectSql = "SELECT user_id, start_date, end_date FROM leave_application WHERE leave_id=?";
                    PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                    selectStmt.setInt(1, leaveId);

                    ResultSet rs = selectStmt.executeQuery();

                    if (!rs.next()) {
                        return "Leave not found";
                    }

                    int userId = rs.getInt("user_id");
                    java.time.LocalDate start = rs.getDate("start_date").toLocalDate();
                    java.time.LocalDate end = rs.getDate("end_date").toLocalDate();

                    int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;

                    // Update leave status
                    String updateSql = "UPDATE leave_application SET status=? WHERE leave_id=?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);

                    updateStmt.setString(1, status);
                    updateStmt.setInt(2, leaveId);
                    updateStmt.executeUpdate();

                    // If approved → update balance
                    if (status.equalsIgnoreCase("Approved")) {

                        String balanceSql = "UPDATE leave_balance SET used_leave = used_leave + ? WHERE user_id=?";
                        PreparedStatement balanceStmt = conn.prepareStatement(balanceSql);

                        balanceStmt.setInt(1, days);
                        balanceStmt.setInt(2, userId);
                        balanceStmt.executeUpdate();
                    }

                    return "Leave " + status + " (" + days + " days)";

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "Failed";
            }).get();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Update leave status task execution failed", e);
        }
    }


    public User getUser(int userId) throws RemoteException {
        try {
            return Multithread.executeTask("Get User Details for ID " + userId, () -> {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM user WHERE user_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String role = rs.getString("role");
                        if ("HR".equalsIgnoreCase(role)) {
                            return new HRUser(
                                rs.getInt("user_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("ic_passport_number"),
                                rs.getString("email"),
                                role
                            );
                        } else {
                            return new EmployeeUser(
                                rs.getInt("user_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("ic_passport_number"),
                                rs.getString("email"),
                                role
                            );
                        }
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
            return Multithread.executeTask("Generate Individual Report (User " + userId + ", Year " + year + ")", () -> {
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
            return Multithread.executeTask("Generate Company Report (Year " + year + ")", () -> {
                // Offloads to the business class which handles the layout and DAO
                return YearlyReportClass.generateCompanyReport(year);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Company Report task execution failed", e);
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
}

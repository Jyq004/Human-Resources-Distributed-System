/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import shared.HRMInterface;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;

public class HRMImplementation extends UnicastRemoteObject implements HRMInterface {

    public HRMImplementation() throws RemoteException {
        super();
    }

        // REGISTER
    public String registerEmployee(String fname, String lname, String ic, String password)
            throws RemoteException {

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO employee(first_name,last_name,ic,password) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, ic);
            ps.setString(4, password);
            ps.executeUpdate();

            return "Employee Registered";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // LOGIN
    public int login(String ic, String password) throws RemoteException {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT emp_id FROM employee WHERE ic=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ic);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("emp_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // APPLY LEAVE
    public String applyLeave(int empId, String type, String start, String end)
            throws RemoteException {

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO leave_request(emp_id,leave_type,start_date,end_date) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, empId);
            ps.setString(2, type);
            ps.setString(3, start);
            ps.setString(4, end);

            ps.executeUpdate();

            return "Leave Applied";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // VIEW STATUS
    public String viewLeaveStatus(int empId) throws RemoteException {

        StringBuilder result = new StringBuilder();

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM leave_request WHERE emp_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.append("LeaveID: ").append(rs.getInt("leave_id"))
                        .append(" Status: ").append(rs.getString("status"))
                        .append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    // CHECK BALANCE
    public String checkLeaveBalance(int empId) throws RemoteException {

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM leave_balance WHERE emp_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total_leave");
                int used = rs.getInt("used_leave");

                return "Remaining Leave: " + (total - used);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No record";
    }
    
    public String testConnection() throws RemoteException {
        return "RMI Connection Successful!";
    }
}
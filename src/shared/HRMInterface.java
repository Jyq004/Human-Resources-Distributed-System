/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.LeaveApplication;
import model.User;

public interface HRMInterface extends Remote {

    User login(String email, String password) throws RemoteException;

    String applyLeave(LeaveApplication leave) throws RemoteException;

    int checkLeaveBalance(int userId) throws RemoteException;

    List<LeaveApplication> getAllLeaves() throws RemoteException;

    String updateLeaveStatus(int leaveId, String status) throws RemoteException;
    
    User getUser(int userId) throws RemoteException;
    
    // (RYAN) Yearly Leave Report Generation
    String generateIndividualReport(int userId, int year) throws RemoteException;
    
    String generateCompanyReport(int year) throws RemoteException;
}

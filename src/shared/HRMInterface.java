/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import model.LeaveApplication;
import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface HRMInterface extends Remote {

    User login(String email, String password) throws RemoteException;

    String applyLeave(LeaveApplication leave) throws RemoteException;

    int checkLeaveBalance(int userId) throws RemoteException;

    List<LeaveApplication> getAllLeaves() throws RemoteException;

    String updateLeaveStatus(int leaveId, String status) throws RemoteException;
    
    User getUser(int userId) throws RemoteException;
}

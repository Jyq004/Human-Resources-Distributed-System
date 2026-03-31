/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HRMInterface extends Remote {
        // HR
    String registerEmployee(String fname, String lname, String ic, String password)
        throws RemoteException;

    // Login
    int login(String ic, String password) throws RemoteException;

    // Employee
    String applyLeave(int empId, String type, String start, String end)
        throws RemoteException;

    String viewLeaveStatus(int empId) throws RemoteException;

    String checkLeaveBalance(int empId) throws RemoteException;
    
    String testConnection() throws RemoteException;
}

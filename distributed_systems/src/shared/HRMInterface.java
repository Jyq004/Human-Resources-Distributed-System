/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

<<<<<<< HEAD
import model.LeaveApplication;
import model.User;
import model.PersonalDetail;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
=======
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.LeaveApplication;
import model.User;
>>>>>>> master

public interface HRMInterface extends Remote {

    User login(String email, String password) throws RemoteException;

    String applyLeave(LeaveApplication leave) throws RemoteException;

    int checkLeaveBalance(int userId) throws RemoteException;

    List<LeaveApplication> getAllLeaves() throws RemoteException;

    String updateLeaveStatus(int leaveId, String status) throws RemoteException;
    
    User getUser(int userId) throws RemoteException;
    
<<<<<<< HEAD
    String registerUser(User user) throws RemoteException;
    
    String updateUser(User user) throws RemoteException;
    
    PersonalDetail getPersonalDetailByUserId(int userId) throws RemoteException;
    
    String updatePersonalDetail(PersonalDetail detail) throws RemoteException;
    
    String changePassword(int userId, String oldPass, String newPass) throws RemoteException;
<<<<<<< HEAD
=======
    // (RYAN) Yearly Leave Report Generation
    String generateIndividualReport(int userId, int year) throws RemoteException;
    
    String generateCompanyReport(int year) throws RemoteException;
>>>>>>> master
=======
    
    List<LeaveApplication> getLeaveHistory(int userId, String status) throws RemoteException;
>>>>>>> 69dde09fb7d5f7823a7b7b106fb72a5d3f3ac6e8
}



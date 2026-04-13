/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;


import shared.HRMInterface;
import model.*;
import ui.Main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("172.20.10.2", 1099);
            HRMInterface service = (HRMInterface) registry.lookup("HRMService");

            javax.swing.SwingUtilities.invokeLater(() -> {
                Main mainUI = new Main(service);
                mainUI.setVisible(true);
            });
            
//            System.out.println("Service: " + service);
//            List<LeaveApplication> leaves = service.getAllLeaves();
//            System.out.println("Leaves count: " + leaves.size());
//            for (LeaveApplication leave : leaves) {
//                User user = service.getUser(leave.getUserId());
//                System.out.println("Leave: " + leave.getLeaveType() + ", User: " + (user != null ? user.getName() : "NULL"));
//            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

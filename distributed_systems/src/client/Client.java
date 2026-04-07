/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;


import shared.HRMInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            String serverIP = "25.41.178.189"; // OR Hamachi IP

            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);
            HRMInterface stub = (HRMInterface) registry.lookup("HRMService");
            
            // test server connection 
            String response = stub.testConnection();
            System.out.println("Server says: " + response);

            Scanner sc = new Scanner(System.in);

            System.out.println("1. Register");
            System.out.println("2. Login");

            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.print("First Name: ");
                String f = sc.next();

                System.out.print("Last Name: ");
                String l = sc.next();

                System.out.print("IC: ");
                String ic = sc.next();

                System.out.print("Password: ");
                String p = sc.next();

                System.out.println(stub.registerEmployee(f, l, ic, p));
            }

            if (choice == 2) {
                System.out.print("IC: ");
                String ic = sc.next();

                System.out.print("Password: ");
                String p = sc.next();

                int empId = stub.login(ic, p);

                if (empId != -1) {
                    System.out.println("Login Success");

                    System.out.println(stub.checkLeaveBalance(empId));
                } else {
                    System.out.println("Login Failed");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

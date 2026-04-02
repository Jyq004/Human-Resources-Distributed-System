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
            String serverIP = "25.41.177.6"; // OR Hamachi IP

            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);
            HRMInterface stub = (HRMInterface) registry.lookup("HRMService");
            
            // test server connection 
            String response = stub.testConnection();
            System.out.println("Server says: " + response);

            javax.swing.SwingUtilities.invokeLater(() -> {
                new LoginClass(stub).setVisible(true);
            });
//
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

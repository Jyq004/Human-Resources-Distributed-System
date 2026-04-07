/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;


import shared.HRMInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        try {
            String serverIP = "25.41.178.189"; // OR Hamachi IP

            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);

            HRMInterface stub = (HRMInterface) registry.lookup("HRMService");

            String response = stub.testConnection();

            System.out.println("Server says: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import shared.HRMInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");

            HRMInterface service = new HRMImplementation();

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("HRMService", service);

            System.out.println("HRM RMI Server Running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
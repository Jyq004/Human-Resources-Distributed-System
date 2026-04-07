/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            HRMImplementation obj = new HRMImplementation();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("HRMService", obj);

            System.out.println("Server started...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
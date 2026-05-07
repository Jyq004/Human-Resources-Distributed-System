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

import javax.rmi.ssl.SslRMIClientSocketFactory;

public class Client {

    public static void main(String[] args) {
        try {
            System.setProperty("javax.net.ssl.trustStore", "hr_client_trust.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "admin123");
            
            Registry registry = LocateRegistry.getRegistry("172.20.10.2", 1099, new SslRMIClientSocketFactory());
            HRMInterface service = (HRMInterface) registry.lookup("HRMService");

            javax.swing.SwingUtilities.invokeLater(() -> {
                Main mainUI = new Main(service);
                mainUI.setVisible(true);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
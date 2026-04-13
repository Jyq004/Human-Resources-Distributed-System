/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import shared.HRMInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.*;
import java.io.*;
import model.Request;
import model.User;
import model.LeaveApplication;
import model.PersonalDetail;

public class Server {

    public static void main(String[] args) {
        
        System.setProperty("javax.net.ssl.keyStore", "hr_server.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "admin123");

        System.setProperty("javax.net.ssl.trustStore", "hr_client_trust.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "admin123");
        
        new Thread(() -> {
            try {
                System.setProperty("java.rmi.server.hostname", "192.168.100.41");

                HRMInterface service = new HRMImplementation();
                
                Registry registry = LocateRegistry.createRegistry(
                        1099,
                        new SslRMIClientSocketFactory(),
                        new SslRMIServerSocketFactory()
                );

                registry.rebind("HRMService", service);

                System.out.println("RMI Server Running...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                SSLServerSocketFactory ssf =
                        (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

                SSLServerSocket serverSocket =
                        (SSLServerSocket) ssf.createServerSocket(5000);

                System.out.println("SSL Socket Server Running on port 5000...");

                HRMImplementation impl = new HRMImplementation();

                while (true) {
                    SSLSocket socket = (SSLSocket) serverSocket.accept();

                    new Thread(() -> {
                        try (
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ) {

                            out.flush();

                            Request req = (Request) in.readObject();
                            String result = "";

                            switch (req.getAction()) {

                                case "REGISTER":
                                    User register = (User) req.getData();
                                    result = impl.registerUser(register);
                                    break;

                                case "UPDATE_PROFILE":
                                    User profile = (User) req.getData();
                                    result = impl.updateUser(profile);
                                    break;

                                case "UPDATE_PERSONAL_DETAIL":
                                    PersonalDetail pd = (PersonalDetail) req.getData();
                                    result = impl.updatePersonalDetail(pd);
                                    break;

                                case "APPLY_LEAVE":
                                    LeaveApplication leave = (LeaveApplication) req.getData();
                                    result = impl.applyLeave(leave);
                                    break;

                                default:
                                    result = "Invalid action";
                            }

                            out.writeObject(result);
                            out.flush();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }        }).start();
    }
}               
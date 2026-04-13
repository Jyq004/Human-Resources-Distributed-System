/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import shared.HRMInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
<<<<<<< HEAD
import java.net.*;
import java.io.*;
import model.Request;
import model.User;
import model.LeaveApplication;
import model.PersonalDetail;
=======
>>>>>>> master

public class Server {

    public static void main(String[] args) {
<<<<<<< HEAD
        new Thread(() -> {
            try {
                System.setProperty("java.rmi.server.hostname", "localhost");

                HRMInterface service = new HRMImplementation();

                Registry registry = LocateRegistry.createRegistry(1099);
                registry.rebind("HRMService", service);

                System.out.println("RMI Server Running...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(5000)) {

                HRMImplementation impl = new HRMImplementation();

                while (true) {
                    Socket socket = serverSocket.accept();

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
            }
        }).start();
    }
}               
=======
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
>>>>>>> master

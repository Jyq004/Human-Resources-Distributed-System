package client;

import shared.HRMInterface;
import model.User;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CodingTest {

    public static void main(String[] args) {
        try {
            // 1. Connect to the RMI Registry
            System.out.println("Connecting to HRM RMI Server...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            HRMInterface service = (HRMInterface) registry.lookup("HRMService");
            
            System.out.println("Success! Connected to server.");

            System.out.println("\n--- Testing Login Functionality ---");
            
            // Simulating multiple concurrent login requests from the client side
            // This will test our new Server Multithread implementation
            Runnable loginTask1 = () -> testLogin(service, "admin@hr.com", "admin123");
            Runnable loginTask2 = () -> testLogin(service, "employee@company.com", "emp123");
            Runnable loginTask3 = () -> testLogin(service, "fake@user.com", "wrongpass");

            Thread t1 = new Thread(loginTask1, "ClientThread-1");
            Thread t2 = new Thread(loginTask2, "ClientThread-2");
            Thread t3 = new Thread(loginTask3, "ClientThread-3");

            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();

            System.out.println("\n--- Multithreaded Login Tests Completed ---");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void testLogin(HRMInterface service, String email, String password) {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Attempting login for: " + email);
            User user = service.login(email, password);
            
            if (user != null) {
                System.out.println("[" + Thread.currentThread().getName() + "] Login SUCCESS: Welcome " + user.getName() + " (Role: " + user.getRole() + ")");
            } else {
                System.out.println("[" + Thread.currentThread().getName() + "] Login FAILED for: " + email);
            }
        } catch (Exception e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Error during login test: " + e.getMessage());
        }
    }
}

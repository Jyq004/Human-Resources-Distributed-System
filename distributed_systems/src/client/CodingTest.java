package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import model.User;
import shared.HRMInterface;

public class CodingTest {

    public static void main(String[] args) {
        try {
            System.out.println("Configuring SSL...");
            System.setProperty("javax.net.ssl.trustStore", "hr_client_trust.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "admin123");

            // 1. Connect to the RMI Registry
            System.out.println("Connecting to HRM RMI Server via SSL...");
            Registry registry = LocateRegistry.getRegistry("192.168.100.41", 1099, new SslRMIClientSocketFactory());
            HRMInterface service = (HRMInterface) registry.lookup("HRMService");
            
            System.out.println("Success! Connected to server.");

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n=== HRM Distributed System Test Menu ===");
                System.out.println("1. Test Connection");
                System.out.println("2. Test Login");
                System.out.println("3. Test Report Generation");
                System.out.println("4. Test Multithreading (40 Threads)");
                System.out.println("5. Exit");
                System.out.print("Select an option: ");
                
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        testConnection(registry);
                        break;
                    case "2":
                        testSingleLogin(service, scanner);
                        break;
                    case "3":
                        testSingleReport(service, scanner);
                        break;
                    case "4":
                        runMultithreadedLogins(service);
                        System.out.println("\n");
                        runMultithreadedReports(service);
                        break;
                    case "5":
                        running = false;
                        System.out.println("Exiting test client.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void testConnection(Registry registry) {
        System.out.println("\n--- Testing Connection ---");
        try {
            String[] boundNames = registry.list();
            boolean found = false;
            for (String name : boundNames) {
                if (name.equals("HRMService")) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.out.println("[Connection Test] SUCCESS: HRMService is bound in the registry.");
            } else {
                System.out.println("[Connection Test] FAILED: HRMService not found in the registry.");
            }
        } catch (Exception e) {
            System.out.println("[Connection Test] ERROR: " + e.getMessage());
        }
    }

    private static void testSingleLogin(HRMInterface service, Scanner scanner) {
        System.out.println("\n--- Testing Single Login ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        testLogin(service, email, password);
    }

    private static void testSingleReport(HRMInterface service, Scanner scanner) {
        System.out.println("\n--- Testing Single Report Generation ---");
        System.out.print("Enter user ID (or press Enter for Company Report): ");
        String userIdStr = scanner.nextLine();
        System.out.print("Enter year: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year format.");
            return;
        }

        if (userIdStr.trim().isEmpty()) {
            testCompanyReport(service, year);
        } else {
            try {
                int userId = Integer.parseInt(userIdStr);
                testIndividualReport(service, userId, year);
            } catch (NumberFormatException e) {
                System.out.println("Invalid User ID format.");
            }
        }
    }

    private static void runMultithreadedLogins(HRMInterface service) {
        System.out.println("\n--- Starting Multithreaded Login Test (40 Threads) ---");
        Thread[] threads = new Thread[40];
        
        for (int i = 0; i < 40; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                // Alternate between a few credential sets
                String email = (threadNum % 2 == 0) ? "admin@hr.com" : "employee@company.com";
                String pass = (threadNum % 2 == 0) ? "admin123" : "emp123";
                
                // Introduce a bad login request periodically to test failed login handling
                if (threadNum % 5 == 0) {
                    email = "fake@user.com";
                    pass = "wrongpass";
                }
                
                testLogin(service, email, pass);
            }, "LoginThread-" + i);
        }

        // Start all threads at once
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("--- Multithreaded Login Test Completed ---");
    }

    private static void runMultithreadedReports(HRMInterface service) {
        System.out.println("\n--- Starting Multithreaded Report Generation Test (40 Threads) ---");
        Thread[] threads = new Thread[40];
        
        for (int i = 0; i < 40; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                // Mix in both Individual and Company Reports among the 40 threads
                if (threadNum % 4 == 0) {
                    testCompanyReport(service, 2026);
                } else {
                    // Alternate between user 1 and 2
                    testIndividualReport(service, (threadNum % 2) + 1, 2026);
                }
            }, "ReportThread-" + i);
        }

        // Start all threads simultaneously
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("--- Multithreaded Report Generation Test Completed ---");
    }

    private static void testLogin(HRMInterface service, String email, String password) {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Attempting login for: " + email);
            User user = service.login(email, password);
            if (user != null) {
                System.out.println("[" + Thread.currentThread().getName() + "] Login SUCCESS: Welcome " + user.getName() + " (" + user.getRole() + ")");
            } else {
                System.out.println("[" + Thread.currentThread().getName() + "] Login FAILED: Invalid credentials for " + email);
            }
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread().getName() + "] Login ERROR: " + e.getMessage());
        }
    }

    private static void testIndividualReport(HRMInterface service, int userId, int year) {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Requesting individual report for user ID: " + userId + " for year: " + year);
            // We ignore parsing out the huge report to the console so our logs remain readable, just get the report
            String report = service.generateIndividualReport(userId, year);
            System.out.println("[" + Thread.currentThread().getName() + "] Individual Report Received. Length: " + report.length());
        } catch (Exception e) {
             System.out.println("[" + Thread.currentThread().getName() + "] Individual Report ERROR: " + e.getMessage());
        }
    }

    private static void testCompanyReport(HRMInterface service, int year) {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Requesting company report for year: " + year);
            String report = service.generateCompanyReport(year);
            System.out.println("[" + Thread.currentThread().getName() + "] Company Report Received. Length: " + report.length());
        } catch (Exception e) {
             System.out.println("[" + Thread.currentThread().getName() + "] Company Report ERROR: " + e.getMessage());
        }
    }
}

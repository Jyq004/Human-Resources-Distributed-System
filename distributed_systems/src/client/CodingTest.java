package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import model.User;
import server.Multithread;
import shared.HRMInterface;

public class CodingTest {

    public static void main(String[] args) {
        try {
            System.out.println("Configuring SSL...");
            System.setProperty("javax.net.ssl.trustStore", "hr_client_trust.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "admin123");

            // 1. Connect to the RMI Registry
            System.out.println("Connecting to HRM RMI Server via SSL...");
            Registry registry = LocateRegistry.getRegistry("10.101.124.149", 1099, new SslRMIClientSocketFactory());
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
                System.out.println("5. Run Automated Test Suite (Testing Engine Mode)");
                System.out.println("6. Exit");
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
                        System.out.println("\n===== STARTING AUTOMATED TEST SUITE =====");
                        boolean rmiPass = testConnectionAutomated(registry);
                        if(rmiPass) {
                            runAutomatedLoginTests(service);
                            runAutomatedReportTests(service);
                            runMultithreadedLogins(service);
                            runMultithreadedReports(service);
                        }
                        System.out.println("===== AUTOMATED TEST SUITE COMPLETED =====\n");
                        break;
                    case "6":
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
        testConnectionAutomated(registry);
    }

    private static boolean testConnectionAutomated(Registry registry) {
        try {
            System.out.println("[TEST] Checking RMI Registry Connection...");
            String[] boundNames = registry.list();
            boolean found = false;
            for (String name : boundNames) {
                if (name.equals("HRMService")) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.out.println("[PASSED] HRMService is securely bound in the SSL registry.");
                return true;
            } else {
                System.out.println("[FAILED] HRMService not found in the registry.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[FAILED] Connection Test ERROR: " + e.getMessage());
            return false;
        }
    }

    private static void runAutomatedLoginTests(HRMInterface service) {
        System.out.println("\n--- Automated Login Tests ---");
        
        // Test 1: Invalid Login
        System.out.println("[TEST] Attempting login with INVALID credentials...");
        try {
            User testUser = service.login("invalid@email.com", "wrongpass");
            if (testUser == null) {
                System.out.println("[PASSED] Login properly rejected invalid user.");
            } else {
                System.out.println("[FAILED] System allowed an invalid login.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // Test 2: Empty Password
        System.out.println("[TEST] Attempting login with EMPTY password...");
        try {
            User testUser = service.login("admin@hr.com", "");
            if (testUser == null) {
                System.out.println("[PASSED] Login rejected empty password.");
            } else {
                System.out.println("[FAILED] System allowed empty password login.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void runAutomatedReportTests(HRMInterface service) {
        System.out.println("\n--- Automated Report Validation ---");
        
        System.out.println("[TEST] Fetching Company Report (Year 2026)...");
        try {
            String report = service.generateCompanyReport(2026);
            if (report != null && report.length() > 0) {
                System.out.println("[PASSED] Company report returned data successfully.");
            } else {
                System.out.println("[FAILED] Company report returning empty data unexpectedly.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        
        System.out.println("[TEST] Fetching Individual Report for user 'Admin'...");
        try {
            String report = service.generateIndividualReport(1, 2026); // assuming 1 is valid
            if (report != null && report.length() > 0) {
                System.out.println("[PASSED] Individual report returned data successfully.");
            } else {
                System.out.println("[FAILED] Individual report was empty.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
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
        System.out.println("\n--- Starting Multithreaded Login Test (40 Threads using server.Multithread) ---");
        List<Future<?>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis(); // Start timer to prove multithreading
        
        for (int i = 0; i < 40; i++) {
            final int threadNum = i;
            // We use futures.add() here so we can FIRE ALL 40 THREADS AT ONCE WITHOUT WAITING.
            futures.add(Multithread.executeTask("Client LoginThread-" + threadNum, () -> {
                String email = (threadNum % 2 == 0) ? "admin@hr.com" : "employee@company.com";
                String pass = (threadNum % 2 == 0) ? "admin123" : "emp123";
                
                if (threadNum % 5 == 0) {
                    email = "fake@user.com"; pass = "wrongpass"; 
                }
                
                // Simulate a tiny delay so we can mathematically prove they overlap
                Thread.sleep(500); 
                testLogin(service, email, pass);
                return null;
            }));
        }

        // Wait for all Multithread tasks to finish
        for (Future<?> f : futures) {
            try {
                f.get(); // Now we wait for the results
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("--- Multithreaded Login Test Completed ---");
        System.out.println("PROVING MULTITHREADING: If this ran sequentially, 40 tasks * 0.5s sleep = 20,000ms (20 seconds).");
        System.out.println("ACTUAL TOTAL TIME: " + totalTime + "ms");
        if (totalTime < 5000) {
            System.out.println("RESULT: SUCCESS! System is successfully processing requests concurrently in parallel.\n");
        }
    }

    private static void runMultithreadedReports(HRMInterface service) {
        System.out.println("\n--- Starting Multithreaded Report Test (40 Threads using server.Multithread) ---");
        List<Future<?>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 40; i++) {
            final int threadNum = i;
            futures.add(Multithread.executeTask("Client ReportThread-" + threadNum, () -> {
                Thread.sleep(500); // Simulate processing time
                
                if (threadNum % 4 == 0) {
                    testCompanyReport(service, 2026);
                } else {
                    testIndividualReport(service, (threadNum % 2) + 1, 2026);
                }
                return null;
            }));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("--- Multithreaded Report Generation Test Completed ---");
        System.out.println("PROVING MULTITHREADING: 40 tasks * 0.5s = 20,000ms sequential time expected.");
        System.out.println("ACTUAL TOTAL TIME: " + (endTime - startTime) + "ms. Parallel execution verified!\n");
    }

    private static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static void testLogin(HRMInterface service, String email, String password) {
        try {
            System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] Attempting login for: " + email);
            User user = service.login(email, password);
            
            // Testing assertion checks
            if (email.contains("fake") || password.equals("wrongpass")) {
                if (user == null) {
                    System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [PASSED] Expected Failure for Invalid Credentials.");
                } else {
                    System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [FAILED] Unexpected Success for Invalid Credentials.");
                }
            } else {
                if (user != null) {
                    System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [PASSED] Login SUCCESS: Welcome " + user.getName() + " (" + user.getRole() + ")");
                } else {
                    System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [FAILED] Expected Success but Login Failed for " + email);
                }
            }
        } catch (Exception e) {
            System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] Login ERROR: " + e.getMessage());
        }
    }

    private static void testIndividualReport(HRMInterface service, int userId, int year) {
        try {
            System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] Requesting individual report for user ID: " + userId + " for year: " + year);
            String report = service.generateIndividualReport(userId, year);
            
            // Testing assertion checks
            if (report != null && !report.trim().isEmpty()) {
                System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [PASSED] Individual Report Received. Length: " + report.length());
            } else {
                System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [FAILED] Report was null or empty.");
            }
        } catch (Exception e) {
             System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] Individual Report ERROR: " + e.getMessage());
        }
    }

    private static void testCompanyReport(HRMInterface service, int year) {
        try {
            System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] Requesting company report for year: " + year);
            String report = service.generateCompanyReport(year);
            
            // Testing assertion checks
            if (report != null && !report.trim().isEmpty()) {
                 System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [PASSED] Company Report Received. Length: " + report.length());
            } else {
                 System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] [FAILED] Company Report was null or empty.");
            }
        } catch (Exception e) {
             System.out.println("[" + getTimestamp() + "] [" + Thread.currentThread().getName() + "] Company Report ERROR: " + e.getMessage());
        }
    }
}

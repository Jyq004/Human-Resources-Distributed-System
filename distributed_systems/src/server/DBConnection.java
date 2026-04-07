/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/rmi_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
            
    public static Connection getConnection() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected!");
            return conn;
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace(); 
            return null;
        }
    }
    
}


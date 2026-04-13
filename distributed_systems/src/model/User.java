/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

public class User implements Serializable {
<<<<<<< HEAD
    
    private static final long serialVersionUID = 1L;
=======
>>>>>>> master

    private int user_id;
    private String first_name;
    private String last_name;
    private String ic_passport_number;
    private String email;
<<<<<<< HEAD
    private String password;
=======
>>>>>>> master
    private String role;

    public User(int user_id, String first_name, String last_name, String ic_passport_number, String email, String role) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.ic_passport_number = ic_passport_number;
        this.email = email;
        this.role = role;
    }

<<<<<<< HEAD
    //Register
    public User(String first_name, String last_name, String ic_passport_number, String email, String password, String role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.ic_passport_number = ic_passport_number;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    //Update
    public User(int user_id, String first_name, String last_name, String ic_passport_number, String email) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.ic_passport_number = ic_passport_number;
        this.email = email;
    }
    
    public int getUserId() { 
        return user_id; 
    }
    
    public String getFirstName() { 
        return first_name; 
    }
    
    public String getLastName() { 
        return last_name; 
    }
    
    public String getName() { 
        return first_name + " " + last_name; 
    }
    
    public String getIc() {
        return ic_passport_number;
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getPassword() { 
        return password; 
    }
        
    public String getRole() { 
        return role; 
    }
=======
    public int getUserId() { return user_id; }
    public String getName() { return first_name + " " + last_name; }
    public String getIc() {return ic_passport_number;}
    public String getRole() { return role; }
>>>>>>> master
}
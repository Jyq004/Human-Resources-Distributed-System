/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
<<<<<<< HEAD
import java.io.Serializable;

public class PersonalDetail implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
=======

public class PersonalDetail {
>>>>>>> master
    private int detail_id;
    private int user_id;
    private String phone_number;
    private String address;
    private String date_of_birth;
<<<<<<< HEAD
=======

>>>>>>> master
    private String contact_name;
    private String relationship;
    private String contact_number;

    public PersonalDetail(int detail_id, int user_id, String phone_number, String contact_name) {
        this.detail_id = detail_id;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.contact_name = contact_name;
    }
<<<<<<< HEAD
    
    public PersonalDetail(int userId, String phoneNumber, String address, String dateOfBirth, String contactName, String relationship, String contactNumber) {
        this.user_id = userId;
        this.phone_number = phoneNumber;
        this.address = address;
        this.date_of_birth = dateOfBirth;
        this.contact_name = contactName;
        this.relationship = relationship;
        this.contact_number = contactNumber;
    }
    
    public PersonalDetail(String phoneNumber, String address, String dateOfBirth, String contactName, String relationship, String contactNumber) {
        this.phone_number = phoneNumber;
        this.address = address;
        this.date_of_birth = dateOfBirth;
        this.contact_name = contactName;
        this.relationship = relationship;
        this.contact_number = contactNumber;
    }
    
    public int getUserId() { 
        return user_id; 
    }
    
    public String getPhoneNumber() { 
        return phone_number; 
    }
    
    public String getAddress() { 
        return address; 
    }
    
    public String getDateOfBirth() { 
        return date_of_birth; 
    }
    
    public String getContactName() { 
        return contact_name; 
    }
    
    public String getRelationship() { 
        return relationship; 
    }
    public String getContactNumber() { 
        return contact_number; 
    }
=======
>>>>>>> master
}

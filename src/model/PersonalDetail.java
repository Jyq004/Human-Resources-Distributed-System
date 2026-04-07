/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class PersonalDetail {
    private int detail_id;
    private int user_id;
    private String phone_number;
    private String address;
    private String date_of_birth;

    private String contact_name;
    private String relationship;
    private String contact_number;

    public PersonalDetail(int detail_id, int user_id, String phone_number, String contact_name) {
        this.detail_id = detail_id;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.contact_name = contact_name;
    }
}

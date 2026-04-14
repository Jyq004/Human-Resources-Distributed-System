/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class HRUser extends User {

    private static final long serialVersionUID = 1L;

    public HRUser(int user_id, String first_name, String last_name, String ic_passport_number, String email, String role) {
        super(user_id, first_name, last_name, ic_passport_number, email, role);
    }

    public HRUser(String first_name, String last_name, String ic_passport_number, String email, String password, String role) {
        super(first_name, last_name, ic_passport_number, email, password, role);
    }
    
    public HRUser(int user_id, String first_name, String last_name, String ic_passport_number, String email) {
        super(user_id, first_name, last_name, ic_passport_number, email);
    }

    @Override
    public void navigateToDashboard(javax.swing.JFrame currentWindow, shared.HRMInterface service) {
        ui.HR_Main hrMain = new ui.HR_Main(service, this);
        hrMain.setVisible(true);
        currentWindow.dispose();
    }
}

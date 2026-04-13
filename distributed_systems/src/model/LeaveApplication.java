/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.time.LocalDate;

public class LeaveApplication implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int leave_id;
    private int user_id;
    private String leave_type;
    private LocalDate start_date;
    private LocalDate end_date;
    private String reason;
    private String status;

    public LeaveApplication(int user_id, String leave_type,
                            LocalDate start_date, LocalDate end_date, String reason) {
        this.user_id = user_id;
        this.leave_type = leave_type;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reason = reason;
    }
    
    public LeaveApplication(int leave_id, int user_id, String leave_type,
                            LocalDate start_date, LocalDate end_date, String reason) {
        this.leave_id = leave_id;
        this.user_id = user_id;
        this.leave_type = leave_type;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reason = reason;
    }
    
    public LeaveApplication(int user_id, String leave_type,
                            LocalDate start_date, LocalDate end_date, String reason, String status) {
        this.user_id = user_id;
        this.leave_type = leave_type;
        this.start_date = start_date;
        this.end_date = end_date;
        this.reason = reason;
        this.status = status;
    }
    
//    //ORIGINAL (YONG JUN)
//    public LeaveApplication(int leave_id, int user_id, String leave_type,
//                            LocalDate start_date, LocalDate end_date, String reason, String status) {
//        this.leave_id = leave_id;
//        this.user_id = user_id;
//        this.leave_type = leave_type;
//        this.start_date = start_date;
//        this.end_date = end_date;
//        this.reason = reason;
//        this.status = status;
//    }
    
    public int getLeaveId() {return leave_id;}
    public int getUserId() { return user_id; }
    public LocalDate getStartDate() { return start_date; }
    public LocalDate getEndDate() { return end_date; }
    public String getLeaveType() { return leave_type; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}
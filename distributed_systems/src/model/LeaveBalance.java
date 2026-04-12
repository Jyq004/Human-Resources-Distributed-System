/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class LeaveBalance {
    private int balance_id;
    private int user_id;
    private int total_leave;
    private int used_leave;

    public LeaveBalance(int balance_id, int user_id, int total_leave) {
        this.balance_id = balance_id;
        this.user_id = user_id;
        this.total_leave = total_leave;
        this.used_leave = 0;
    }

    public int getRemainingLeave() {
        return total_leave - used_leave;
    }

    public boolean canApplyLeave(int days) {
        return getRemainingLeave() >= days;
    }

    public void addUsedLeave(int days) {
        used_leave += days;
    }
}
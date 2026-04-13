/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author jason
 */
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String action;
    private Object data;

    public Request(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public Object getData() {
        return data;
    }
}

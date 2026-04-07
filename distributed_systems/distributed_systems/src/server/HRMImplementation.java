/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import shared.HRMInterface;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class HRMImplementation extends UnicastRemoteObject implements HRMInterface {

    public HRMImplementation() throws RemoteException {
        super();
    }

    public String testConnection() throws RemoteException {
        return "RMI Connection Successful!";
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HRMInterface extends Remote {
    String testConnection() throws RemoteException;
}

package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ProjectRI extends Remote {

    String registerUser(String username, String password) throws RemoteException;
    String loginUser(String username, String password) throws RemoteException;
}

package edu.ufp.inf.sd.rmi.project.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {
    String getUsername() throws RemoteException;

    void update() throws RemoteException;
}

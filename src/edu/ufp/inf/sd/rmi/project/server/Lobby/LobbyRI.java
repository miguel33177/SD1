package edu.ufp.inf.sd.rmi.project.server.Lobby;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyRI extends Remote {
    void removeObserver(ObserverRI o) throws RemoteException;

    void registerObserver(ObserverRI o) throws RemoteException;

    void notifyObservers() throws RemoteException;


}

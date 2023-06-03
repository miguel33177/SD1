package edu.ufp.inf.sd.rmi.project.server.Lobby;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyRI extends Remote {
    String getState() throws RemoteException;

    void setState(String state) throws RemoteException;

    boolean isGameOn() throws RemoteException;

    void setGameOn(boolean gameOn) throws RemoteException;

    String getMap() throws RemoteException;

    List<ObserverRI> getObservers() throws RemoteException;

    void removeObserver(ObserverRI o) throws RemoteException;

    void registerObserver(ObserverRI o) throws RemoteException;

    void notifyObservers() throws RemoteException;

    void notifyGameStarting() throws RemoteException;


}

package edu.ufp.inf.sd.rabbitmqservices.project.consumer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {
    void start() throws RemoteException;

    //Channel getChannel() throws RemoteException;

    //Channel getChannel() throws RemoteException;

    void setId(int id) throws RemoteException;

    int getCharacter() throws RemoteException;

    String getLobby() throws RemoteException;

    String getUsername() throws RemoteException;

    void setLobby(String lobby) throws RemoteException;

    void update() throws RemoteException;

    void lobbyClosed() throws RemoteException;
}

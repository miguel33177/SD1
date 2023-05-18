package edu.ufp.inf.sd.rmi.project.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI{
    private final String username;

    private String lobby;

    public ObserverImpl(String u,String l) throws RemoteException {
        this.username = u;
        this.lobby = l;
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public void update() throws RemoteException{

    }
}

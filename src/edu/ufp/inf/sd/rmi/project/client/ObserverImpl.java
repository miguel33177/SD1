package edu.ufp.inf.sd.rmi.project.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI{

    public ObserverImpl() throws RemoteException {
    }

    @Override
    public void update() throws RemoteException{

    }
}

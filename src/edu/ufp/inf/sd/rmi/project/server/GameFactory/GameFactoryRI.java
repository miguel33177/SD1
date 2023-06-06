package edu.ufp.inf.sd.rmi.project.server.GameFactory;

import edu.ufp.inf.sd.rmi.project.server.GameSession.GameSessionRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface GameFactoryRI extends Remote {

    void saveHashMap() throws RemoteException;

    void loadHashMap() throws RemoteException;

    boolean login(String username,String password) throws RemoteException;

    boolean register(String username,String password) throws RemoteException;

    String generateToken(String username, String password, Date expirationDate) throws RemoteException;

    GameSessionRI getSession() throws RemoteException;

    void logout(String u) throws RemoteException;

    boolean hasChannel() throws RemoteException;
}

package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface ProjectRI extends Remote {

    String registerUser(String username, String password) throws RemoteException;
    String loginUser(String username, String password) throws RemoteException;
    void createLobby(String mapName) throws RemoteException;
    void joinLobby(String lobbyName) throws RemoteException;
    public ArrayList<Lobby> getArrayLobbies() throws RemoteException;
    public ArrayList<String> getLobbies() throws RemoteException;
    public ArrayList<Integer> getLobbiesCurrPlayers() throws RemoteException;
    public ArrayList<Integer> getLobbiesMaxPlayers() throws RemoteException;

}

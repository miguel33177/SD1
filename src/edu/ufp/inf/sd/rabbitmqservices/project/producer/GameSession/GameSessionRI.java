package edu.ufp.inf.sd.rabbitmqservices.project.producer.GameSession;

import edu.ufp.inf.sd.rabbitmqservices.project.consumer.ObserverRI;
import edu.ufp.inf.sd.rabbitmqservices.project.producer.Lobby.LobbyImpl;
import edu.ufp.inf.sd.rabbitmqservices.project.producer.Lobby.LobbyRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface GameSessionRI extends Remote {
    LobbyRI getLobby(String l) throws RemoteException;
    String createLobby(String mapName, ObserverRI o) throws RemoteException;

    void leaveLobby(String l, ObserverRI o) throws RemoteException;
    boolean checkLobbyAvailable(String l) throws RemoteException;
    String joinLobby(String l, ObserverRI o) throws RemoteException;
    ArrayList<LobbyImpl> getArrayLobbies() throws RemoteException;
    ArrayList<String> getLobbies() throws RemoteException;
    ArrayList<Integer> getLobbiesCurrPlayers() throws RemoteException;
    ArrayList<Integer> getLobbiesMaxPlayers() throws RemoteException;

    void deleteLobby() throws RemoteException;
}

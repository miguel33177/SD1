package edu.ufp.inf.sd.rmi.project.server.GameSession;

import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyImpl;
import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameSessionRI extends Remote {
    LobbyRI createLobby(String mapName) throws RemoteException;

}

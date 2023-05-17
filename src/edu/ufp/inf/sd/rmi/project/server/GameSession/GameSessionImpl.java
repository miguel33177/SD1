package edu.ufp.inf.sd.rmi.project.server.GameSession;

import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyImpl;
import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyRI;

import javax.security.auth.Subject;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {
    private HashMap<String, LobbyImpl> lobbies;
    private ArrayList<LobbyImpl> arrayLobbies;

    public GameSessionImpl() throws RemoteException {
        this.lobbies = new HashMap<>();
        arrayLobbies = new ArrayList<>();
    }

    @Override
    public LobbyRI createLobby(String mapName) throws RemoteException{
        LobbyImpl lobby = new LobbyImpl(mapName);
        String lobbyName = mapName + "#" + lobby.getId();
        lobbies.put(lobbyName, lobby);
        arrayLobbies.add(lobby);
        return lobby;
    }

}

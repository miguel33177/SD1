package edu.ufp.inf.sd.rmi.project.server.GameSession;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;
import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyImpl;
import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyRI;

import javax.security.auth.Subject;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {
    private HashMap<String, LobbyImpl> lobbies;
    private ArrayList<LobbyImpl> arrayLobbies;

    public GameSessionImpl(HashMap<String,LobbyImpl> h, ArrayList<LobbyImpl> l)throws RemoteException {
        super();
        lobbies = h;
        arrayLobbies = l;
    }

    @Override
    public LobbyRI getLobby(String l) throws RemoteException{
        return lobbies.get(l);
    }

    @Override
    public String createLobby(String mapName, ObserverRI o) throws RemoteException{
           LobbyImpl lobby = new LobbyImpl(mapName);
           String lobbyName = mapName + "#" + lobby.getId();
           lobbies.put(lobbyName, lobby);
           arrayLobbies.add(lobby);
           lobby.registerObserver(o);
           return lobbyName;
    }

    @Override
    public void leaveLobby(String l, ObserverRI o) throws RemoteException{
        LobbyImpl lobby = lobbies.get(l);
        lobby.removeObserver(o);
        if(lobby.getObservers().size() == 0) {
            lobbies.remove(l);
            arrayLobbies.remove(lobby);
        }
    }

    @Override
    public String joinLobby(String l, ObserverRI o) throws RemoteException {
        int startIndex = l.indexOf("-") + 1;
        l = l.substring(startIndex).trim();
        LobbyImpl lobby = lobbies.get(l);
        if(lobby.getObservers().size() < lobby.getMaxPlayers()) {
            o.setLobby(l);
            lobby.registerObserver(o);
            return l;
        }
        return null;
    }

    @Override
    public ArrayList<LobbyImpl> getArrayLobbies() throws RemoteException {
        return arrayLobbies;
    }

    @Override
    public ArrayList<String> getLobbies() throws RemoteException{
        ArrayList<String> x = new ArrayList<>();
        String s;
        for(LobbyImpl l : this.getArrayLobbies()){
            s = l.getMap() + "#" + l.getId();
            x.add(s);
        }
        return x;
    }

    @Override
    public ArrayList<Integer> getLobbiesMaxPlayers() throws RemoteException{
        ArrayList<Integer> maxPlayers = new ArrayList<>();
        for(LobbyImpl l : getArrayLobbies()){
            maxPlayers.add(l.getMaxPlayers());
        }
        return maxPlayers;
    }

    @Override
    public ArrayList<Integer> getLobbiesCurrPlayers() throws RemoteException{
        ArrayList<Integer> currPlayers = new ArrayList<>();
        for(LobbyImpl l : getArrayLobbies()){
            currPlayers.add(l.getObservers().size());
        }
        return currPlayers;
    }

    @Override
    public boolean checkLobbyAvailable(String l) throws RemoteException {
        int startIndex = l.indexOf("-") + 1;
        l = l.substring(startIndex).trim();
        LobbyImpl lobby = lobbies.get(l);
        return lobby.getObservers().size() < lobby.getMaxPlayers();
    }
}

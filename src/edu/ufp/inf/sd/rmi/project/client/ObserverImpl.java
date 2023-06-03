package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI{
    private final String username;

    private String lobby;

    private int character;

    private Game game;

    public ObserverImpl(String u, int character, Game g) throws RemoteException {
        this.username = u;
        this.character = character;
        this.game = g;
    }

    @Override
    public int getCharacter() throws RemoteException{
        return character;
    }

    @Override
    public String getLobby() throws RemoteException{
        return lobby;
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }
    @Override
    public void setLobby(String lobby) throws RemoteException {
        this.lobby = lobby;
    }

    @Override
    public void update() throws RemoteException{

    }

    @Override
    public void start() throws RemoteException{
        System.out.println(getLobby());
        game.start(this.lobby);
    }
}

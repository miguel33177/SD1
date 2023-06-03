package edu.ufp.inf.sd.rmi.project.server.Lobby;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;
import edu.ufp.inf.sd.rmi.project.server.TokenRing;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class LobbyImpl extends UnicastRemoteObject implements LobbyRI{
    private static int idCounter = 0;
    private int id;

    private List<ObserverRI> observers;

    private int maxPlayers;

    private String map;
    private String lobbyName;

    private boolean gameOn = false;

    private TokenRing tokenRing;

    private String state;


    public LobbyImpl(String map) throws RemoteException{
        idCounter++;
        this.id = idCounter;
        this.observers = Collections.synchronizedList(new ArrayList<>());
        if(Objects.equals(map, "FourCorners")){
            this.maxPlayers = 4;
        }
        if(Objects.equals(map,"SmallVs")){
            this.maxPlayers = 2;
        }
        this.lobbyName = map + "#" + this.id;
        this.map=map;

    }

    public int getId(){
        return this.id;
    }

    @Override
    public String getState() throws RemoteException {
        return state;
    }

    @Override
    public void setState(String state) throws RemoteException{
        this.state = state;
    }

    @Override
    public boolean isGameOn() throws RemoteException{
        return gameOn;
    }

    @Override
    public void setGameOn(boolean gameOn) throws RemoteException {
        this.gameOn = gameOn;
    }

    @Override
    public String getMap() throws RemoteException{
        return map;
    }

    @Override
    public List<ObserverRI> getObservers() throws RemoteException {
        return observers;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void registerObserver(ObserverRI o) throws RemoteException {
        if(this.getObservers().size() < maxPlayers){
            this.getObservers().add(o);
        }
        if(this.getObservers().size() == maxPlayers){
            notifyGameStarting();
        }
    }

    @Override
    public void notifyGameStarting() throws RemoteException{
        setGameOn(true);
        this.tokenRing = new TokenRing(this.maxPlayers);
        for(ObserverRI x : this.observers){
            try {
                x.start();
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeObserver(ObserverRI o) throws RemoteException {
        this.getObservers().remove(o);
    }

    @Override
    public void notifyObservers() throws RemoteException{
        for (ObserverRI o: this.getObservers()) {
            o.update();
        }
    }
}

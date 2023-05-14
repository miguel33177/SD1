package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Battle;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observer;

public class Lobby {
    private static int idCounter = 0;

    private int id;

    private ArrayList<Observer> observers;

    private int maxPlayers;

    private boolean onGoing;

    private int numPlayers;

    private String map;

    public int getId(){
        return this.id;
    }



    public Lobby(String map){
        this.idCounter++;
        this.id = idCounter;
        if(Objects.equals(map, "FourCorners")){
            this.maxPlayers = 4;
        }
        if(Objects.equals(map,"SmallVs")){
            this.maxPlayers = 2;
        }
        this.observers = new ArrayList<>();
        this.numPlayers = 1;
        this.map = map;
        this.onGoing = false;
    }


    public String getMap() {
        return map;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayers() {
        return numPlayers;
    }

    public void incrementNumPlayers() {
        numPlayers++;
    }
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers){
            //observer.update();
        }
    }

    public static void main(String[] args) {
        Lobby l1 = new Lobby("FourCorners");
        Lobby l2 = new Lobby("FourCorners");
        Lobby l3 = new Lobby("FourCorners");
        Lobby l4 = new Lobby("SmallVs");
        System.out.println(l1.getId());
        System.out.println(l2.getId());
        System.out.println(l3.getId());
        System.out.println(l4.getId());
    }

}

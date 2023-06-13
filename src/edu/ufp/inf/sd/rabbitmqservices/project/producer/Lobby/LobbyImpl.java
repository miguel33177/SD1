package edu.ufp.inf.sd.rabbitmqservices.project.producer.Lobby;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.rabbitmqservices.project.consumer.ObserverRI;
import edu.ufp.inf.sd.rabbitmqservices.project.producer.TokenRing;

import java.io.IOException;
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

    private transient Channel channel;

    private String FAN_OUT;

    private String W_QUEUE;

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

    public LobbyImpl(String map, Channel c) throws RemoteException{
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
        this.channel = c;
        this.FAN_OUT = "FANOUT_LOBBY#" + this.getId();
        this.W_QUEUE = "W_LOBBY#" + this.getId();
    }

    @Override
    public String getFAN_OUT() throws RemoteException{
        return this.FAN_OUT;
    }

    @Override
    public String getW_QUEUE() throws RemoteException{
        return this.W_QUEUE;
    }

    public void createQueues() {
        try {
            channel.exchangeDeclare(this.FAN_OUT, "fanout");
            channel.queueDeclare(this.W_QUEUE + this.getId(),false,false,false,null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenQueues() {
        DeliverCallback deliver = (consumerTag, delivery) ->{
            String[] msg = new String(delivery.getBody(),"UTF-8").split(",");

            String action = msg[0];
            int obs = Integer.parseInt(msg[1]);

            if(this.tokenRing.getHolder() == obs){
                this.channel.basicPublish(this.FAN_OUT,"",null,action.getBytes("UTF-8"));
                if (action.equals("passTurn")){
                    this.tokenRing.passToken();
                }
            }
        };
        try {
            this.channel.basicConsume(this.W_QUEUE,true,deliver, consumerTag ->{});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public int getId(){
        return this.id;
    }

    @Override
    public String getState() throws RemoteException {
        return state;
    }

    @Override
    public void setState(String state, ObserverRI o) throws RemoteException{
        if(this.tokenRing.getHolder() == this.observers.indexOf(o)){
            this.state = state;
            this.notifyObservers();
            if(state.compareTo("passTurn") == 0){
                this.tokenRing.passToken();
            }
        }
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
            o.setId(this.getObservers().indexOf(o));
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
        if(this.channel != null) {
            this.createQueues();
            this.listenQueues();
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

    @Override
    public void closeGame() throws RemoteException{
        for(ObserverRI obs : observers){
            obs.lobbyClosed();
        }
        observers.clear();
    }

    @Override
    public ObserverRI getCurrentPlayer() throws RemoteException{
        return this.observers.get(this.tokenRing.getHolder());
    }
}

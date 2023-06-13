package edu.ufp.inf.sd.rabbitmqservices.project.consumer;

import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.engine.Game;
import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.menus.MenuHandler;
import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.menus.Pause;
import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.players.Base;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

import com.rabbitmq.client.*;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI{
    public transient Connection con;
    public transient Channel channel;

    private int id;
    private final String username;

    private String lobby;

    private int character;

    private Game game;

    public ObserverImpl(String u, int character, Game g) throws RemoteException, Exception {
        this.username = u;
        this.character = character;
        this.game = g;
        //bind();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.con = factory.newConnection();
        this.channel = con.createChannel();
        this.channel.queueDeclare("queue_w",false,false,false,null);
        listenQueue();
    }

    public void bind(){

    }

    public int getId(){
        return id;
    }




    @Override
    public void setId(int id) throws RemoteException{
        this.id = id;
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
        String move = Game.gameSession.getLobby(this.lobby).getState();
        if (Game.GameState==Game.State.PLAYING) {
            Base ply = Game.player.get(Game.btl.currentplayer);
            if (Objects.equals(move, "up")) {
                ply.selecty--;
                if (ply.selecty<0) {
                    ply.selecty++;
                }
            }
            else if (Objects.equals(move, "down")) {
                ply.selecty++;
                if (ply.selecty>=Game.map.height) {
                    ply.selecty--;
                }
            }
            else if (Objects.equals(move, "left")) {
                ply.selectx--;
                if (ply.selectx<0) {
                    ply.selectx++;
                }
            }
            else if (Objects.equals(move, "right")) {
                ply.selectx++;
                if (ply.selectx>=Game.map.width) {
                    ply.selectx--;
                }
            }
            else if (Objects.equals(move, "select")) {
                Game.btl.Action();
            }
            else if (Objects.equals(move, "cancel")) {
                Game.player.get(Game.btl.currentplayer).Cancle();
            }
            else if (Objects.equals(move, "start")) {
                if(Game.gameSession.getLobby(Game.o.getLobby()).getCurrentPlayer() == Game.o){
                    new Pause();
                }
            }
            else if (Objects.equals(move, "passTurn")) {
                MenuHandler.CloseMenu();
                Game.btl.EndTurn();
            }
            else{
                String[] arr = move.split(":");
                int item = Integer.parseInt(arr[0]);
                int x = Integer.parseInt(arr[1]);
                int y = Integer.parseInt(arr[2]);
                Game.btl.Buyunit(item, x, y);

            }
        }
    }

    @Override
    public void start() throws RemoteException{
        System.out.println(getLobby());
        //game.start();
        if(Game.gameFactory.hasChannel()){
            listenQueue();
        }
    }

    @Override
    public void lobbyClosed() throws RemoteException{
        Game.gui.closeInGameScreen();
    }

    public void listenQueue(){
        try {
            /*
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            this.con = factory.newConnection();
            this.channel = con.createChannel();
            */

           this.channel.exchangeDeclare("fan_out", "fanout");
            String queueName = this.channel.queueDeclare().getQueue();
            System.out.println(queueName);
            //this.channel.queueDeclarePassive("fan_out").getQueue();
            //this.channel.queueBind("queue_w", "worker", "");
            //this.channel.queueDeclare("queue_w",false,false,false,null);

            channel.queueBind(queueName, "fan_out", "");


            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                this.updateRabbit(message);
            };
            this.channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRabbit(String move) {
        if (Game.GameState == Game.State.PLAYING) {
            Base ply = Game.player.get(Game.btl.currentplayer);
            if (Objects.equals(move, "up")) {
                ply.selecty--;
                if (ply.selecty<0) {
                    ply.selecty++;
                }
            }
            else if (Objects.equals(move, "down")) {
                ply.selecty++;
                if (ply.selecty>=Game.map.height) {
                    ply.selecty--;
                }
            }
            else if (Objects.equals(move, "left")) {
                ply.selectx--;
                if (ply.selectx<0) {
                    ply.selectx++;
                }
            }
            else if (Objects.equals(move, "right")) {
                ply.selectx++;
                if (ply.selectx>=Game.map.width) {
                    ply.selectx--;
                }
            }
            else if (Objects.equals(move, "select")) {
                Game.btl.Action();
            }
            else if (Objects.equals(move, "cancel")) {
                Game.player.get(Game.btl.currentplayer).Cancle();
            }
            else if (Objects.equals(move, "start")) {
                new Pause();
            }
            else if (Objects.equals(move, "passTurn")) {
                MenuHandler.CloseMenu();
                Game.btl.EndTurn();
            }
            else{
                String[] arr = move.split(":");
                int item = Integer.parseInt(arr[1]);
                int x = Integer.parseInt(arr[2]);
                int y = Integer.parseInt(arr[3]);
                Game.btl.Buyunit(item, x, y);

            }
        }
    }
}

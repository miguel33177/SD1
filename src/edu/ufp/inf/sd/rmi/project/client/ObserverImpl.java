package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;
import edu.ufp.inf.sd.rmi.project.client.awgame.menus.MenuHandler;
import edu.ufp.inf.sd.rmi.project.client.awgame.menus.ModeOnline;
import edu.ufp.inf.sd.rmi.project.client.awgame.menus.Pause;
import edu.ufp.inf.sd.rmi.project.client.awgame.players.Base;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.awt.*;

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
        game.start(this.lobby);
    }

    @Override
    public void lobbyClosed() throws RemoteException{
        Game.gui.closeInGameScreen();
    }
}

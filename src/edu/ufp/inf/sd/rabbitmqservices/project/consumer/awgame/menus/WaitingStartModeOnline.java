package edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.menus;

import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.engine.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class WaitingStartModeOnline implements ActionListener {
    JButton leaveLobby = new JButton("Leave lobby");
    private String lobbyName;

    public WaitingStartModeOnline(String lobbyName) {
        this.lobbyName = lobbyName;
        Point size = MenuHandler.PrepMenu(400, 280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddListeners();
        AddGui();
    }

    private void SetBounds(Point size) {
        leaveLobby.setBounds(size.x+110, size.y + 170, 150, 32);
    }

    private void AddGui() {
        JLabel waitingLabel1 = new JLabel("Waiting to start game " + this.lobbyName);
        JLabel waitingLabel2 = new JLabel("Please wait for the game to start.");
        waitingLabel1.setFont(new Font("Arial", Font.BOLD, 18));
        waitingLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingLabel1.setForeground(Color.WHITE);
        waitingLabel2.setForeground(Color.WHITE);

        waitingLabel1.setBounds(170, 100, 380, 20);
        waitingLabel2.setBounds(200, 130, 250, 20);



        Game.gui.add(waitingLabel1);
        Game.gui.add(waitingLabel2);
        Game.gui.add(leaveLobby);
    }

    private void AddListeners() {
        leaveLobby.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if( s == leaveLobby){
            try {
                Game.gameSession.leaveLobby(lobbyName,Game.o);
                new ModeOnline();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

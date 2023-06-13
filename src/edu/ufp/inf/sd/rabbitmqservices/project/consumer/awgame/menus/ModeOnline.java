package edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.menus;

import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.engine.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModeOnline implements ActionListener {

    public JButton New = new JButton("New Lobby");
    public JButton Join = new JButton("Join Lobby");
    public JButton Return = new JButton("Return");
    public JList games_list = new JList();
    DefaultListModel games_model = new DefaultListModel();

    public ModeOnline() {
        Point size = MenuHandler.PrepMenu(400,280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddListeners();
        AddGui();
    }
    private void SetBounds(Point size) {
        New.setBounds(size.x,size.y+10, 100, 32);
        Join.setBounds(size.x,size.y+10+38*1, 100, 32);
        Return.setBounds(size.x,size.y+10+38*5, 100, 32);


    }
    private void AddGui() {
        Game.gui.add(New);
        Game.gui.add(Join);
        Game.gui.add(Return);
    }
    private void AddListeners() {
        New.addActionListener(this);
        Join.addActionListener(this);
        Return.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s==New) {new MapsSelectionModeOnline();}
        else if (s==Join) {new JoinLobbyModeOnline();}
        else if (s==Return) {
            MenuHandler.CloseMenu();
            Game.gui.LoginScreen();}
    }
}

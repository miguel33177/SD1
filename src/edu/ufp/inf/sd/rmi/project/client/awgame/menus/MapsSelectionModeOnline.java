package edu.ufp.inf.sd.rmi.project.client.awgame.menus;

import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapsSelectionModeOnline implements ActionListener {

    public JButton Return = new JButton("Return");
    public JButton Next = new JButton("Next");
    public JList maps_list = new JList();
    DefaultListModel maps_model = new DefaultListModel();

    public MapsSelectionModeOnline() {
        Point size = MenuHandler.PrepMenu(400, 280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddGui();
        AddListeners();
        MapList(size);
    }

    private void SetBounds(Point size) {
        Next.setBounds(size.x, size.y + 10, 100, 32);
        Return.setBounds(size.x, size.y + 10 + 38 * 6, 100, 32);

    }

    private void AddGui() {
        Game.gui.add(Return);
        Game.gui.add(Next);
    }

    private void MapList(Point size) {
        maps_model = Game.finder.GrabMaps();
        JScrollPane maps_pane = new JScrollPane(maps_list = new JList(maps_model));
        maps_pane.setBounds(size.x + 220, size.y + 10, 140, 260);//220,10
        Game.gui.add(maps_pane);
        maps_list.setBounds(0, 0, 140, 260);
        maps_list.setSelectedIndex(0);
    }
    private void AddListeners() {
        Next.addActionListener(this);
        Return.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s == Next) {
            new PlayerSelectionModeOnline(maps_list.getSelectedValue() + "", false,true);
        } else if (s == Return) {
            new ModeOnline();
        }
    }
}




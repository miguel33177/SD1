package edu.ufp.inf.sd.rmi.project.client.awgame.menus;

import edu.ufp.inf.sd.rmi.project.client.ProjectClient;
import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class JoinLobbyModeOnline implements ActionListener{

    public JButton Return = new JButton("Return");
    public JButton Next = new JButton("Next");
    public JList lobbies_list = new JList();
    DefaultListModel lobbies_model = new DefaultListModel();
    //  private HashMap<String, Lobby> lobbies;

    public JoinLobbyModeOnline() {
        Point size = MenuHandler.PrepMenu(400, 280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddGui();
        AddListeners();
        LobbyList(size);
    }

    private void SetBounds(Point size) {
        Next.setBounds(size.x, size.y + 10, 100, 32);
        Return.setBounds(size.x, size.y + 10 + 38 * 6, 100, 32);

    }

    private void AddGui() {
        Game.gui.add(Next);
        Game.gui.add(Return);
    }


    private void LobbyList(Point size) {
        JScrollPane maps_pane = new JScrollPane(lobbies_list);
        maps_pane.setBounds(size.x + 220, size.y + 10, 140, 260);
        Game.gui.add(maps_pane);
        lobbies_list.setModel(lobbies_model);
        lobbies_list.setBounds(0, 0, 140, 260);
        lobbies_list.setSelectedIndex(0);


        try {
            ArrayList<String> lobbies = Game.gameSession.getLobbies();
            ArrayList<Integer> lobbyCurrPlayers =Game.gameSession.getLobbiesCurrPlayers();
            ArrayList<Integer> lobbyMaxPlayers = Game.gameSession.getLobbiesMaxPlayers();

            if (lobbies.isEmpty()) {
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());

                JLabel messageLabel = new JLabel("No lobbies available");
                panel.add(messageLabel, BorderLayout.CENTER);

                JButton backButton = new JButton("Go Back");
                panel.add(backButton, BorderLayout.SOUTH);

                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Window window = SwingUtilities.getWindowAncestor(backButton);
                        window.dispose();
                        new ModeOnline();
                    }
                });

                JOptionPane.showOptionDialog(
                        Game.gui,
                        panel,
                        "Aviso",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[] {},
                        null
                );
            } else {
                for (int i = 0; i < lobbies.size(); i++) {
                    String lobby = lobbies.get(i);
                    int currPlayers = lobbyCurrPlayers.get(i);
                    int maxPlayers = lobbyMaxPlayers.get(i);
                    String lobbyInfo = currPlayers + "/" + maxPlayers + " - " + lobby;
                    lobbies_model.addElement(lobbyInfo);
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    private void AddListeners() {
        Next.addActionListener(this);
        Return.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s==Next) { new PlayerSelectionModeOnline(lobbies_list.getSelectedValue() + "", true,false);}
        else if (s==Return) {new ModeOnline();}
    }
}

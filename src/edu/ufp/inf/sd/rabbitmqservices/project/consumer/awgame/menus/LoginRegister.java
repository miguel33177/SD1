package edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.menus;

import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.engine.Game;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class LoginRegister implements ActionListener{
    public JTextField username = new JTextField("Username");
    public JTextField password = new JTextField("Password");
    public JButton login = new JButton("Login");
    public JButton register = new JButton("Register");
    public JLabel usernameLabel = new JLabel("Username:");
    public JLabel passwordLabel = new JLabel("Password:");

    public LoginRegister() {
        Point size = MenuHandler.PrepMenu(400,280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddListeners();
        AddGui();
    }

    private void SetBounds(Point size) {
        usernameLabel.setBounds(size.x, size.y + 10 + 38, 100, 32);
        passwordLabel.setBounds(size.x, size.y + 10 + 38 * 2, 100, 32);
        username.setBounds(size.x + 150, size.y + 10 + 38, 100, 32);
        password.setBounds(size.x + 150, size.y + 10 + 38 * 2, 100, 32);
        login.setBounds(size.x+150, size.y + 10 + 38 * 3+20, 100, 32);
        register.setBounds(size.x+150, size.y + 10 + 38 * 4+20, 100, 32);

        usernameLabel.setForeground(Color.WHITE);
        passwordLabel.setForeground(Color.WHITE);
    }
    private void AddGui() {
        Game.gui.add(usernameLabel);
        Game.gui.add(passwordLabel);
        Game.gui.add(username);
        Game.gui.add(password);
        Game.gui.add(login);
        Game.gui.add(register);

    }

    private void AddListeners() {
        login.addActionListener(this);
        register.addActionListener(this);
        username.addActionListener(this);
        password.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s == login) {
            String user = username.getText();
            String pass = password.getText();
            try {
                if(Game.gameFactory.login(user, pass)){
                    Game.username = user;
                    Game.gameSession = Game.gameFactory.getSession();
                    new StartMenu();
                }else {
                    JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if (s == register) {
            String user = username.getText();
            String pass = password.getText();
            try {
                if (Game.gameFactory.register(user, pass)){
                    Game.username = user;
                    JOptionPane.showMessageDialog(null, "Register complete", "Success", JOptionPane.ERROR_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
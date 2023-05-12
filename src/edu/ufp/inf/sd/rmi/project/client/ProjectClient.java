package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;
import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Gui;
import edu.ufp.inf.sd.rmi.project.client.awgame.menus.StartMenu;
import edu.ufp.inf.sd.rmi.project.server.ProjectRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectClient {

    private SetupContextRMI contextRMI;
    private ProjectRI projectRI;
    public static String token;
    private Game game;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            ProjectClient client = new ProjectClient(args);
            client.lookupService();
            client.playService();
        }
    }

    public ProjectClient(String args[]) {
        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(ProjectClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void lookupService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);
                projectRI = (ProjectRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playService() {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;

            while (true) {
                System.out.println("Press 'r' for register or 'l' for login:");
                input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("r")) {
                    register();
                    break;
                } else if (input.equalsIgnoreCase("l")) {
                    login();
                    break;
                } else {
                    System.out.println("Try again");
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void register() throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scanner.nextLine().trim();

        System.out.println("Enter password:");
        String password = scanner.nextLine().trim();

        // Chamar o método remoto registerUser() do serviço ProjectRI
        String response = projectRI.registerUser(username, password);

        System.out.println(response);
    }

    private void login() throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scanner.nextLine().trim();

        System.out.println("Enter password:");
        String password = scanner.nextLine().trim();

        // Chamar o método remoto loginUser() do serviço ProjectRI
        String response = projectRI.loginUser(username, password);
        System.out.println(response);
        if (response.equals("Login bem sucedido.")) {
            this.game = new Game();
        }
    }

}
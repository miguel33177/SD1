package edu.ufp.inf.sd.rabbitmqservices.project.client;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;
import edu.ufp.inf.sd.rmi.project.server.GameFactory.GameFactoryRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectClient {

    private SetupContextRMI contextRMI;
    private GameFactoryRI gameFactoryRI;
    private Connection connection;
    private Channel channel;

    private void lookupService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);
                gameFactoryRI = (GameFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void initContext(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            String exchangeName = "gameExchanger";
            channel.exchangeDeclare(exchangeName, "topic");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        //int port = 5672;
        System.out.println(args);

        System.out.println(Arrays.toString(args));
        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            this.contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }
    public ProjectClient(String[] args) {
        this.initContext(args);
        this.lookupService();
        this.playService();
    }
    private void playService() {new Game(this.gameFactoryRI);}

    public static void main(String[] args) {
       ProjectClient client = new ProjectClient(args);
    }
}

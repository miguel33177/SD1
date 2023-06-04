//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.ufp.inf.sd.rabbit.project.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;
import edu.ufp.inf.sd.rmi.project.server.GameFactory.GameFactoryRI;
import edu.ufp.inf.sd.rmi.project.server.ProjectServer;
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
            Registry registry = this.contextRMI.getRegistry();
            if (registry != null) {
                String serviceUrl = this.contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);
                this.gameFactoryRI = (GameFactoryRI)registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (NotBoundException | RemoteException var3) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, (String)null, var3);
        }

    }

    private void initContext(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        String registryIP;
        try {
            this.connection = factory.newConnection();
            this.channel = this.connection.createChannel();
            registryIP = "Exchanger";
            this.channel.exchangeDeclare(registryIP, "topic");
        } catch (TimeoutException | IOException var7) {
            throw new RuntimeException(var7);
        }

        System.out.println(args);
        System.out.println(Arrays.toString(args));

        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            this.contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException var6) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, (String)null, var6);
        }

    }

    public ProjectClient(String[] args) {
        this.initContext(args);
        this.lookupService();
        this.playService();
    }

    private void playService() {
        new Game(this.gameFactoryRI, channel);
    }

    public static void main(String[] args) {
        new ProjectClient(args);
    }
}
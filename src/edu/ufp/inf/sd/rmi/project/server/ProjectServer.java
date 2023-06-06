package edu.ufp.inf.sd.rmi.project.server;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rmi.project.server.GameFactory.GameFactoryImpl;
import edu.ufp.inf.sd.rmi.project.server.GameFactory.GameFactoryRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectServer {

    private GameFactoryRI gameFactoryRI;

    private SetupContextRMI contextRMI;
    private transient Connection connection;
    private transient Channel channel;

    public ProjectServer(String args[]) {
        try {
            // List and set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            // Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName,"a"});
        } catch (RemoteException e) {
            Logger.getLogger(ProjectServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            // Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            // Create Servant
            this.gameFactoryRI = new GameFactoryImpl();
            // Register remote object
            if (registry != null) {
                // Get service url
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to bind service @ {0}", serviceUrl);
                // Rebind service on rmiregistry and wait for calls
                registry.rebind(serviceUrl, this.gameFactoryRI);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                // registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
             if (args != null && args.length < 2) {
                 System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.HelloWorldServer <rmi_registry_ip> <rmi_registry_port> <service_name>");
                 System.exit(-1);
             } else {
                 // 1. ============ Setup server RMI context ============
                 ProjectServer hws = new ProjectServer(args);
                 // 2. ============ Rebind service ============
                 hws.setConnection();
                 hws.rebindService();

             }
         }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.ufp.inf.sd.rabbit.project.server;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectServer {
    private transient Connection connection;
    private transient Channel channel;

    public ProjectServer() {
    }

    public void setConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();
        this.channel.queueDeclare("serverQueues", false, false, false, null);
        DeliverCallback deliverCallbackTopic = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "MESSAGE RECEIVED:" + message);
            String routeKey = "server";
            this.channel.basicPublish("Exchanger", routeKey, null, message.getBytes(StandardCharsets.UTF_8));
        };
        this.channel.basicConsume("serverQueues", true, deliverCallbackTopic, (consumerTag) -> {
        });
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ProjectServer projectServer = new ProjectServer();
        projectServer.setConnection();
    }
}
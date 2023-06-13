package edu.ufp.inf.sd.rabbitmqservices.project.consumer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.project.consumer.awgame.engine.Game;
import edu.ufp.inf.sd.rabbitmqservices.project.producer.GameFactory.GameFactoryRI;
import edu.ufp.inf.sd.rabbitmqservices.util.rabbit.RabbitUtils;
import edu.ufp.inf.sd.rabbitmqservices.util.rmisetup.SetupContextRMI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Recv {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private Integer id;
    public Recv() throws IOException, TimeoutException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] args) throws Exception {
            Recv client = new Recv();
            //client.initContext();
            String res = client.call();
            //System.out.println(res);
            client.playService(Integer.parseInt(res));
    }


    private void initContext() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String exchangeName = "gameExchanger";
            channel.exchangeDeclare(exchangeName, "topic");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void Context() throws IOException, TimeoutException {
        Connection connection = RabbitUtils.newConnection2Server("localhost", 5672, "guest", "guest");
        Channel channel = RabbitUtils.createChannel2Server(connection);

        String hello = "hello";

    }


    public String call() throws IOException, InterruptedException, ExecutionException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, "hello".getBytes("UTF-8"));

        final CompletableFuture<String> response = new CompletableFuture<>();

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.complete(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = null;
        try {
            result = response.get(20, TimeUnit.SECONDS); // Espera até 5 segundos pela resposta
        } catch (TimeoutException e) {
            // Tempo limite atingido, tratamento de erro adequado aqui
        } finally {
            channel.basicCancel(ctag);
        }

        return result;
    }


    private void playService(int id) throws Exception {
        new Game(id);
    }
}

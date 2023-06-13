package edu.ufp.inf.sd.rabbitmqservices.project.producer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.rabbit.RabbitUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Send {
    private static Integer players = 0;
    Connection connection;
    Channel channel;

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static final String FANOUT = "fan_out";

    private static final String W_QUEUE = "queue_w";

    private TokenRing ring;

    public Send() throws Exception{
        this.ring = new TokenRing(2);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);

        channel.basicQos(1);
    }

    public static void main(String[] argv) throws Exception {
        Send server = new Send();

        System.out.println(" [x] Awaiting RPC requests");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = players.toString();
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                if (message.equals("hello")){
                    server.channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    server.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    players++;
                }

            } catch (RuntimeException e) {
                System.out.println(" [.] " + e);
            }
        };

        server.channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));

        server.channel.exchangeDeclare(FANOUT, "fanout");
        server.channel.queueDeclare(W_QUEUE,false,false,false,null);
        System.out.println("HEYYY");
        DeliverCallback deliver = (consumerTag, delivery) ->{
            String[] msg = new String(delivery.getBody(),"UTF-8").split(",");

            String action = msg[0];
            int obs = Integer.parseInt(msg[1]);
            if(Objects.equals(action, "buy")){
                action = "buy:" + msg[2];
                System.out.println(action);
            }

            if(server.ring.getHolder() == obs){
                server.channel.basicPublish(FANOUT,"",null,action.getBytes("UTF-8"));
                if (action.equals("passTurn")){
                    server.ring.passToken();
                }
            }
        };
        try {
            server.channel.basicConsume(W_QUEUE,true,deliver, consumerTag ->{});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

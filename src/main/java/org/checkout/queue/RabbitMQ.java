package org.checkout.queue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

public class RabbitMQ implements MessageQueue{


    private Connection connection;    
    private Channel channel;
    public final static String DELAYED_QUEUE_NAME = "delayed_queue";
    public final static String QUEUE_NAME = "queue";
   
    public static class BasicConsumer extends DefaultConsumer {
        
        RabbitMQ queue;
        
        public BasicConsumer(Channel channel, RabbitMQ queue) {
            super(channel);
            this.queue = queue;
        }
        
        
        @Override
        public void handleDelivery(String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body) throws IOException
        {
            String message = new String(body);
            System.out.println(" [x] Received '" + message + "'");
            
            RetryRequest retryRequest  = new RetryRequest();
            
            if (!retryRequest.workOnMessage(message)) {
                queue.enqueue(message, 10);
            }
            
            // Ack the message. 
            System.out.println("Ack the message\n");
            this.getChannel().basicAck(envelope.getDeliveryTag(), false);
            
        }
        
    }
    
    @Override
    public void registerCallback() throws IOException {
        
        // ExecutorService executorService = Executors.newFixedThreadPool(20);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        channel.queueDeclare(RabbitMQ.DELAYED_QUEUE_NAME, true, false, false, null);
        channel.basicQos(0);
        
        BasicConsumer consumer= new BasicConsumer(channel, this);
        channel.basicConsume(RabbitMQ.DELAYED_QUEUE_NAME, false, consumer);
        
        System.out.println("Started the connection to rabbitMQ");
    }

    @Override
    public void cancelCallback() throws IOException {
        channel.close();
        connection.close();
        
        System.out.println("Stopped the connection to rabbitMQ");
        
    }

    @Override
    public void enqueue(String message, int delay) throws IOException {

        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-message-ttl", delay*1000);
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", RabbitMQ.DELAYED_QUEUE_NAME );
        channel.queueDeclare(RabbitMQ.QUEUE_NAME, true, false, false, arguments);
            
        channel.basicPublish("", RabbitMQ.QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
      
        
    }

}

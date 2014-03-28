package org.test.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import testing.MessagePublishing;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.yammer.dropwizard.lifecycle.Managed;

public class MessageProcessing  implements Managed{
    
    
    private Connection connection;
    
    private Channel channel;
   
    
    public static class BasicConsumer extends DefaultConsumer {

        private MessageProcessing msgProcessing;
        
        public BasicConsumer(Channel channel, MessageProcessing msgProcessing) {
            super(channel);
            this.msgProcessing = msgProcessing;
        }
        
        
        @Override
        public void handleDelivery(String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body)
                        throws IOException
        {
            String message = new String(body);
            System.out.println(" [x] Received '" + message + "'");
            
            RetryRequest retryRequest  = new RetryRequest();
            
            if (retryRequest.workOnMessage(message)) {
                // Ack the message. 
                System.out.println("Ack the message\n");
                this.getChannel().basicAck(envelope.getDeliveryTag(), false);
            }
            
            else {
                
                msgProcessing.publishMessage(message);
                
                // Nack the message.
                // System.out.println("Nack the message\n");
                //this.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
            }
            
        }
        
    }

  

    @Override
    public void start() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        channel.queueDeclare(MessagePublishing.DELAYED_QUEUE_NAME, true, false, false, null);
        channel.basicQos(0);
        
        BasicConsumer consumer= new BasicConsumer(channel, this);
        channel.basicConsume(MessagePublishing.DELAYED_QUEUE_NAME, false, consumer);
        
        System.out.println("Started the connection to rabbitMQ");
        
    }

    @Override
    public void stop() throws Exception {
        channel.close();
        connection.close();
        
        System.out.println("Stopped the connection to rabbitMQ");
        
    }
    
    
    public void publishMessage(String message) throws IOException {
        
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-message-ttl", 5000);
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", MessagePublishing.DELAYED_QUEUE_NAME );
        channel.queueDeclare(MessagePublishing.QUEUE_NAME, true, false, false, arguments);
            
        channel.basicPublish("", MessagePublishing.QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
    }
    
}

package org.test.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
public class MessagePublishing {
    
    public final static String QUEUE_NAME = "queue";
    
    public final static String DELAYED_QUEUE_NAME = "delayed_queue";
    
    public static void main(String[] argv) throws IOException {
        publishMessage("" + DateTime.now().getMillis());
    }

    public static void publishMessage(String message) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(DELAYED_QUEUE_NAME, true, false, false, null);
        
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-message-ttl", 5000);
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", DELAYED_QUEUE_NAME );
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        
        channel.close();
        connection.close();
    }

}

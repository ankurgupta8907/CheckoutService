package org.checkout.testing;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.checkout.queue.RabbitMQ;
import org.joda.time.DateTime;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
public class MessagePublishing implements Runnable{
    
    /**
     * Shared common object pool.
     */
    public static ObjectPool<Channel> pool;
    
    private Thread t;
    private String threadName;
    
    public MessagePublishing(String name) {
        threadName = name;
        System.out.println("Creating " +  threadName );
    }
    
    public void start ()
    {
       System.out.println("Starting " +  threadName);
       if (t == null)
       {
          t = new Thread (this, threadName);
       }
       t.start ();
    }
    
    public Thread getThread()
    {
        return t;
    }
    
    public static void main(String[] argv) throws IOException, InterruptedException {
       
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setTestOnBorrow(true);
        pool = new GenericObjectPool<Channel>(new ChannelFactory(), config);
        long start = System.currentTimeMillis();
        
        initiateMessages();
        System.out.println("Idle instances = " + pool.getNumIdle());
        initiateMessages();
        long end = System.currentTimeMillis();;
        
        System.out.println("Answer = " + (end - start)/1000);
    }

    private static void initiateMessages() throws InterruptedException {
        List<MessagePublishing>  list = new ArrayList<MessagePublishing>();
        
        for (int i=0; i<4; i++) {
            MessagePublishing msgPublish = new MessagePublishing(""+i);
            list.add(msgPublish);
            msgPublish.start();
        }
        
        for (int i=0; i<list.size(); i++) {
            list.get(i).getThread().join();;
        }
    }

    public void publishMessage(String message) throws NoSuchElementException, IllegalStateException, Exception {

        Channel channel = null;
        try {
            channel = pool.borrowObject();
            
            System.out.println("Channel code " + channel.hashCode());
            channel.queueDeclare(RabbitMQ.DELAYED_QUEUE_NAME, true, false, false, null);
        
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put("x-message-ttl", 5000);
            arguments.put("x-dead-letter-exchange", "");
            arguments.put("x-dead-letter-routing-key", RabbitMQ.DELAYED_QUEUE_NAME );
            channel.queueDeclare(RabbitMQ.QUEUE_NAME, true, false, false, arguments);
            
            channel.basicPublish("", RabbitMQ.QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            channel.close();
            Thread.sleep(1000);
        }
        finally {
            pool.returnObject(channel);
        }
        
    }

    @Override
    public void run() {
        try {
            publishMessage("" + DateTime.now().getMillis());
            System.out.println(threadName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

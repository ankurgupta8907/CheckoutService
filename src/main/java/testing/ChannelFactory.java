package testing;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ChannelFactory extends BasePooledObjectFactory<Channel>{

    @Override
    public Channel create() throws Exception {
        System.out.println("Creating channel");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        return channel;
    }

    @Override
    public PooledObject<Channel> wrap(Channel obj) {
        return new DefaultPooledObject<Channel>(obj);
    }
    
    @Override
    public boolean validateObject(PooledObject<Channel> obj) {
        System.out.println("VALIDATION " + obj.getObject().hashCode() + "  " + obj.getObject().isOpen());
        return obj.getObject().isOpen();
    }
    

}

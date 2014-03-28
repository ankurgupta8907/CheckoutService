package org.checkout.managed;

import org.checkout.queue.MessageQueue;
import org.checkout.queue.RabbitMQ;

import com.yammer.dropwizard.lifecycle.Managed;

public class MessageProcessing  implements Managed{
    
    private MessageQueue queue;
    
    public MessageProcessing() {
        queue = new RabbitMQ();
    }
    
    @Override
    public void start() throws Exception {

        // queue register callback.
        queue.registerCallback();
        
    }

    @Override
    public void stop() throws Exception {
        // queue cancel register
        queue.cancelCallback();
        
    }
    
}

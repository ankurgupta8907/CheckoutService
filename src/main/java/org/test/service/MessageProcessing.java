package org.test.service;

import queue.MessageQueue;

import com.yammer.dropwizard.lifecycle.Managed;

public class MessageProcessing  implements Managed{
    
    MessageQueue queue;
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

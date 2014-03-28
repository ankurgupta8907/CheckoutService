package queue;

public interface MessageQueue {
    
    /**
     * Register callback that listens to queue.
     * @throws Exception
     */
    public void registerCallback() throws Exception;
    
    /**
     * Cancel callback that listens to queue.
     * @throws Exception
     */
    public void cancelCallback() throws Exception;
    
    /**
     * Enqueue the message.
     * @param message
     * @param delay the time in seconds after which message should resurface.
     */
    public void enqueue(String message, int delay) throws Exception;

}

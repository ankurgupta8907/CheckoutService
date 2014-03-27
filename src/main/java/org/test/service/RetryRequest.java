package org.test.service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.http.HttpStatus;

public class RetryRequest {
    
    private MessageProcessing messageProcessing;
    
    public RetryRequest(MessageProcessing messageProcessing) {
        this.messageProcessing = messageProcessing;
    }

    public void workOnMessage(String message)  {
        
        try {
            URL obj = new URL("http://localhost:8080/checkout?time=" + message);
            
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            
            if (HttpStatus.INTERNAL_SERVER_ERROR_500 == responseCode) { 
                messageProcessing.publishMessage(message);
            }
        } 
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}

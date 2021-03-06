package org.checkout.queue;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.http.HttpStatus;

public class RetryRequest {

    /**
     * Returns true is the message was successfully processed and false otherwise.
     * @param message
     * @return
     */
    public boolean workOnMessage(String message)  {
        
        try {
            
            // This whole thing should be done by the thick client of checkout service.
            URL obj = new URL("http://localhost:8080/checkout?time=" + message);
            
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            
            if (HttpStatus.INTERNAL_SERVER_ERROR_500 == responseCode) { 
                return false;
            }
            return true;
        } 
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

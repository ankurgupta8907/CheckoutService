package org.checkout.service;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

@Path("/checkout")
@Produces(MediaType.APPLICATION_JSON)
public class CheckoutResource {

    private final AtomicLong counter;

    public CheckoutResource() {
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Response checkTime(@QueryParam("time") Optional<String> time)
            throws Exception {
        
        String status = "Completed";
        
        if (time.isPresent()) {
            String timeVal = time.get();
            DateTime prevTime = new DateTime(Long.parseLong(timeVal)+25000);
            DateTime curTime = DateTime.now();
            
            if (!curTime.isAfter(prevTime)) {
                throw new TimeException("Not Completed");
            }
        }
        return new Response(counter.incrementAndGet(), status);
    }

}
package com.intechsouthwest.ordermanagementsystem.helpers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by gregorylaflash on 8/18/16.
 */
public class HelperException extends WebApplicationException {

    public HelperException(String message){
        super(Response.status(Response.Status.PRECONDITION_FAILED).type(MediaType.TEXT_PLAIN).entity(message).build());
    }
}

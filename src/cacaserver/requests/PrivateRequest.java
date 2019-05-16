/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.requests;

import cacaserver.controller.Context;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class PrivateRequest 
{
    private String requester;
    private String requested;
    
    public PrivateRequest(JsonObject args, Socket sender, Context context)
    { 
        requester = args.get("requester").getAsString();
        requested = args.get("request").getAsString();
        
        Hashtable<Socket, String> connected = context.getConnectedUsers();
        
        JsonObject req = new JsonObject();
        req.addProperty("type", "new-private");
        req.add("args", args);
        
        synchronized(connected)
        {
            connected.forEach((_s,_u)->
            {
                if(_u.equals(requested))
                {
                    
                    try 
                    {
                        _s.getOutputStream().write(new Gson().toJson(req).getBytes());
                        
                        JsonObject nR = new JsonObject();
                        nR.addProperty("type", "ok");
                        nR.addProperty("status", true);
                        
                        sender.getOutputStream().write(new Gson().toJson(nR).getBytes());
                        
                    } catch (IOException ex) 
                    {
                        try {
                            JsonObject nR = new JsonObject();
                            nR.addProperty("type", "exit");
                            nR.addProperty("status", false);
                            
                            sender.getOutputStream().write(new Gson().toJson(nR).getBytes());
                            Logger.getLogger(PrivateRequest.class.getName()).log(Level.SEVERE, null, ex);
                            
                        } catch (IOException ex1) {
                            Logger.getLogger(PrivateRequest.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            });
            connected.notify();
        }
        
    }
    
}

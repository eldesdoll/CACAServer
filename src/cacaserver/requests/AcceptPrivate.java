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
public class AcceptPrivate {
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta función es usada para aceptar solicitudes de conversación
     * privada, es decir, que no son amigos.
     */
    public AcceptPrivate(JsonObject args, Socket sender, Context context)
    {
        String requester = args.get("requester").getAsString();
        
        Hashtable<Socket, String> conn = context.getConnectedUsers();
       
        synchronized(conn)
        {
            conn.forEach((_s,_u)->
            {
                if(_u.equals(requester))
                {
                    try 
                    {
                        JsonObject req = new JsonObject();
                        req.addProperty("type", "accept-private");
                        req.add("args",args);
                        _s.getOutputStream().write(new Gson().toJson(req).getBytes());
                        
                    } catch (IOException ex) 
                    {
                        Logger.getLogger(AcceptPrivate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            conn.notify();
        }
    }
    
}

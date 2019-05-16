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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class Refresh 
{
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion realiza el mismo procedimiento
     * que en login, de manera que se devuelven todos
     * los datos necesarios para crear la vista principal.
     * 
     */
    public Refresh(JsonObject args, Socket sender,Context context)
    {
        try {
            JsonObject resp = new JsonObject();
            resp.addProperty("type","refresh");
            resp.add("args",new Login(context).updateArgs(args.get("username").getAsString()));
            sender.getOutputStream().write(new Gson().toJson(resp).getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Refresh.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}

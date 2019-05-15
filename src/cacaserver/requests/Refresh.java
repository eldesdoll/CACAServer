/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.requests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.Socket;

/**
 *
 * @author ivan_
 */
public class Refresh 
{
    public Refresh(JsonObject args, Socket sender,Context context)
    {
        JsonObject resp = new JsonObject();
        resp.addProperty("type","refresh");
        resp.add("args",new Login(context).updateArgs(args.get("username").getAsString()));
        sender.getOutputStream().write(new Gson().toJson(resp).getBytes());
    }
    
}

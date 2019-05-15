/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.controller;

import cacaserver.requests.AcceptRequest;
import cacaserver.requests.FriendRequest;
import cacaserver.requests.Login;
import cacaserver.requests.NewGroup;
import cacaserver.requests.Sign;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.Socket;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class ProcessRequest 
{
    private static JsonParser parser;
    private static Logger logger;
    
    static 
    {
        parser = new JsonParser();
        logger = Logger.getLogger("ProcessRequest");
    }
    
    /***
     * Constructor de request
     * @param request String del JsonObject inicial
     * @param sender Quien envió el mensaje
     * @param context Por si necesitan enviar respuestas, broadcasts, etc...
     * Es un fácil acceso al Server fuera del server. 
     */
    public static void processRequest(String request, Socket sender, Context context) 
    {
        logger.info("Got a package from "+sender.getInetAddress());
        JsonObject response = parser.parse(request).getAsJsonObject();
        switch(response.get("type").getAsString())
        {
            case "login":
                Login login = new Login(response.get("args").getAsJsonObject(),sender, context); 
                break;
            case "sign":
                Sign sign = new Sign(response.get("args").getAsJsonObject(),sender);
                break;
            case "friend-request":
                FriendRequest friend = new FriendRequest(response.get("args").getAsJsonObject(),sender, context);
                break;
            case "accept-request":
                AcceptRequest req = new AcceptRequest(response.get("args").getAsJsonObject(),sender, context);
                break;
            case "newGroup":
                System.out.println("aqui");
                NewGroup nGroup = new NewGroup(response.get("args").getAsJsonObject(), sender);
                break;
            default:
                break;
        }
    }
}
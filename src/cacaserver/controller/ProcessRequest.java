/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.controller;

import cacaserver.requests.AcceptGroupRequest;
import cacaserver.requests.AcceptRequest;
import cacaserver.requests.AddToGroup;
import cacaserver.requests.DeleteGroup;
import cacaserver.requests.FriendRequest;
import cacaserver.requests.GetConversation;
import cacaserver.requests.GetGroupConversation;
import cacaserver.requests.Login;
import cacaserver.requests.ModifyGroup;
import cacaserver.requests.NewGroup;
import cacaserver.requests.NewPersonalMssg;
import cacaserver.requests.Refresh;
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
        JsonObject args = response.get("args").getAsJsonObject();
        switch(response.get("type").getAsString())
        {
            case "login":
                new Login(args,sender, context); 
                break;
            case "sign":
                new Sign(args,sender);
                break;
            case "friend-request":
                new FriendRequest(args,sender, context);
                break;
            case "accept-request": //Aceptar amigo 
                new AcceptRequest(args,sender, context);
                break;
            case "newGroup": //Nuevo grupo 
                new NewGroup(args, sender,context);
                break;
            case "add-to-group":  //Agregar al grupo
                new AddToGroup(args, sender, context);
                break;
            case "accept-group-request": //Aceptar solicitud de grupo
                new AcceptGroupRequest(args,sender,context);
                break;
            case "refresh": //Refrescar 
                new Refresh(args,sender,context);
                break;
            case "deleteGroup": //Borrar grupo
                new DeleteGroup (args, sender, context);
                break;
            case "modifyGroup": //Cambiar asunto
                new ModifyGroup (args, sender, context);
                break;
            case "newPersonal": //Mensaje personal individual
                new NewPersonalMssg (args, sender, context);
                break;
            case "getPersonal": //Obtener mensajes chat normal 
                new GetConversation(args,sender, context);
                break;
            case "getGroup": //Obtener mensajes chat grupal
                new GetGroupConversation(args,sender,context);
                break;
            default:
                break;
        }
    }
}
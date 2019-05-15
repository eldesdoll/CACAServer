/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.controller;

import cacaserver.requests.Login;
import cacaserver.requests.Sign;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.Socket;

/**
 *
 * @author ivan_
 */
public class ProcessRequest 
{
    private static JsonParser parser;
    
    static 
    {
        parser = new JsonParser();
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
        System.out.println("Recibí info");
        JsonObject response = parser.parse(request).getAsJsonObject();
        switch(response.get("type").getAsString())
        {
            case "login":
                Login login = new Login(response.get("args").getAsJsonObject(),sender, context); 
                /**
                 * Aquí sí necesitaré el contexto, porque requiero enviar los
                 * usuarios conectados (si hay éxito) (que no puedo obtener directamente de la DB)
                 */
                break;
            case "sign":
                Sign sign = new Sign(response.get("args").getAsJsonObject(),sender);
                break;
            default:
                break;
        }
    }
}
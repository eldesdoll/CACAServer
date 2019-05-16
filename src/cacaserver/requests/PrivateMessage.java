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
public class PrivateMessage 
{
   private String destinatario;
   private String remitente;
   private String mensaje;
   
   /**
    * 
    * @param args
    * @param sender
    * @param context 
    * 
    * Esta funcion intenta enviar un mensaje priavado.
    * La funcion envia el mensaje, el remitente y el 
    * destinatario al cliente correspondiente.
    */
   public PrivateMessage(JsonObject args, Socket sender, Context context)
   {
       destinatario = args.get("destinatario").getAsString();
       remitente = args.get("remitente").getAsString();
       mensaje = args.get("mensaje").getAsString();
       
       Hashtable<Socket, String> users = context.getConnectedUsers();
       
       synchronized(users)
       {
           users.forEach((_s,_u)->
           {
               if(destinatario.equals(_u) || remitente.equals(_u))
               {
                   try {
                       JsonObject req = new JsonObject();
                       req.addProperty("type", "privateMessage");
                       JsonObject nArgs = new JsonObject();
                       nArgs.addProperty("remitente", remitente);
                       nArgs.addProperty("mensaje", mensaje);
                       nArgs.addProperty("destinatario", destinatario);
                       req.add("args", nArgs);
                       
                       _s.getOutputStream().write(new Gson().toJson(req).getBytes());
                   } catch (IOException ex) {
                       Logger.getLogger(PrivateMessage.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
           });
           users.notify();
       }
   }
}

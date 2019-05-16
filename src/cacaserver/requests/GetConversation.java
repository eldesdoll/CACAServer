/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.requests;

import cacaserver.controller.Context;
import cacaserver.database.Database;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class GetConversation 
{

    private String username;
    private String dest;
    private Connection connection;
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion obtiene los datos enviadosp por el cliente
     * y llamda a la funcion getConversation para intentar obtener
     * la conversacion entre dos personas. La funcion prepara y 
     * envia una respuesta marcando si la operación de la base de 
     * datos fue exitosa con un verdader o en caso contrario con un
     * falso.
     */
    public GetConversation(JsonObject args, Socket sender, Context context) 
    {
        try {
            connection = Database.getConnection();
            username = args.get("user1").getAsString();
            dest = args.get("user2").getAsString();
            
            JsonObject response = new JsonObject();
            response.addProperty("type","responseChat");
            response.addProperty("with",dest);
            response.add("args", getConversation());

            sender.getOutputStream().write(new Gson().toJson(response).getBytes());
        } catch (IOException ex) {
            Logger.getLogger(GetConversation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * La funcion obtiene todos los mensajes enviados entre dos personas
     * y los guarda en un array json.
     * @return 
     * Esta funcion retorna el array de json anteriormente generado.
     */
    public JsonArray getConversation() 
    {
        JsonArray array = new JsonArray();
        try {
            String query = "SELECT * FROM chatamigo WHERE (remitente = '" + username + "' AND "
                    + "destinatario = '" + dest + "') OR (destinatario = '" + username + "' AND"
                    + " remitente = '" + dest + "') ORDER BY hora ASC";
            System.out.println(query);
            PreparedStatement select = connection.prepareStatement(query);
            ResultSet result = select.executeQuery();
            while(result.next())
            {
                JsonObject message = new JsonObject();
                message.addProperty("sender", result.getString("remitente"));
                message.addProperty("message", result.getString("mensaje"));
                array.add(message);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetConversation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }
}

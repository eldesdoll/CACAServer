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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class GetGroupConversation {
    private String id;
    private Connection connection;
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion obtiene los datos enviados por el cliente
     * e intenta obtener toda la conversación de un grupo. En
     * caso de que se haya podido obtener la conversación, la
     * función prepara y envia una respuesta marcando como verdadero
     * y en caso de que la consulta haya presentado un error, como
     * falso.
     */
    public GetGroupConversation(JsonObject args, Socket sender, Context context)
    {
        try {
            id = args.get("id").getAsString();
            connection = Database.getConnection();
            
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "responseGroup");
            resp.addProperty("id", id);
            
            resp.add("args", getConversation());
            
            sender.getOutputStream().write(new Gson().toJson(resp).getBytes());
            
        } catch (IOException ex) {
            Logger.getLogger(GetGroupConversation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @return 
     * Esta función intenta obtener todos los mensajes enviados
     * en un grupo guardados en la base de datos. En caso de haberlos
     * obtenidos, estos se guardan en un array json, el cual es retornado
     * al final de la función. En caso de que haya fallado al consulta
     * se regresa un arreglo json vacío.
     */
    private JsonArray getConversation()
    {
        JsonArray array = new JsonArray();
        try {
            String query = "SELECT * FROM chatgrupo WHERE grupo = "+id+
                    " ORDER BY hora ASC";
            ResultSet result = connection.prepareStatement(query).executeQuery();
            while(result.next())
            {
                JsonObject message = new JsonObject();
                message.addProperty("sender", result.getString("remitente"));
                message.addProperty("message", result.getString("mensaje"));
                array.add(message);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetGroupConversation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }    
}

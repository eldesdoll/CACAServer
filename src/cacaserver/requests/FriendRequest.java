/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.requests;

import cacaserver.controller.Context;
import cacaserver.database.Database;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class FriendRequest 
{ 
    String user;
    String petition;
    Connection connection; 
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion recibe los datos enviados por el
     * cliente, e intenta crear una nueva amistad
     * entre dos usuarios. La función prepara y envia 
     * una respuesta que marca con un verdadero en caso de que
     * la insercióna la base de datos fue exitosa o en un caso
     * contrario con un falso.
     * 
     */
    public FriendRequest(JsonObject args, Socket sender, Context context)
    {
        try {
            user = args.get("user").getAsString();
            petition = args.get("request").getAsString();
            
            JsonObject request = new JsonObject();
            request.addProperty("type", "friend-request");
            request.addProperty("status", addRequest());
            
            Login login = new Login(context);
            request.add("args", login.updateArgs(user));
            
            Gson gson = new Gson();
            
            String response = gson.toJson(request);
            
            sender.getOutputStream().write(response.getBytes());
            
        } catch (IOException ex) 
        {
            Logger.getLogger(FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean addRequest()
    {
        try {
            connection = Database.getConnection();
            String query = "INSERT INTO amistad(propietario, amigo ) VALUES ('"+user+"','"+petition+"')";
            PreparedStatement result = connection.prepareStatement(query);
            result.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            Database.returnConnection(connection);
        }
    }
}

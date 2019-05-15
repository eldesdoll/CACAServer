/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.requests;

import cacaserver.controller.Context;
import cacaserver.database.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    
    public GetGroupConversation(JsonObject args, Socket sender, Context context)
    {
        id = args.get("id").getAsString();
        connection = Database.getConnection();
        
    }
    
    private JsonArray getConversation()
    {
        JsonArray array = new JsonArray();
        try {
            String query = "SELECT * FROM chatgrupo WHERE grupo = "+id+
                    "ORDER BY hora DESC";
            ResultSet result = connection.prepareStatement(query).executeQuery();
            while(result.next())
            {
                JsonObject message = new JsonObject();
                message.addProperty("sender", result.get);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetGroupConversation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }
    
}

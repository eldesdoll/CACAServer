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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class AcceptGroupRequest 
{
    private String id;
    private String username;
    private boolean status;
    private Connection  connection;
    
    public AcceptGroupRequest(JsonObject args, Socket sender, Context context)
    {
        try {
            connection = Database.getConnection();
            id = args.get("id").getAsString();
            username = args.get("username").getAsString();
            status = args.get("status").getAsBoolean();
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "accept-group-request");
            response.addProperty("status", accept());
            
            Login login = new Login(context);
            response.add("args",login.updateArgs(username));
            
            Gson gson = new Gson();
            
            sender.getOutputStream().write(gson.toJson(response).getBytes());
            Database.returnConnection(connection);
        } catch (IOException ex) {
            Logger.getLogger(AcceptGroupRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean accept()
    {
        try {
            String status = this.status ? "'aceptado'" : "'rechazado'";
            String query = "UPDATE miembro SET estado = "+status+" WHERE "
                    + "grupo = "+id+" AND usuario = '"+username+"'";
            connection.prepareStatement(query).execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AcceptGroupRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}

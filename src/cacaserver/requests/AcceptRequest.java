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
public class AcceptRequest {
    
    private boolean status;
    private String friend;
    private String owner;
    
    public AcceptRequest(JsonObject args, Socket sender, Context context)
    {
        try {
            status =  args.get("status").getAsBoolean();
            owner = "'"+args.get("username").getAsString()+"'";
            friend = "'"+args.get("friend").getAsString()+"'";
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "exit");
            response.addProperty("status", accept());
            
            Login login = new Login(context);
            response.add("args",login.updateArgs(args.get("username").getAsString()));
            
            Gson gson = new Gson();
            
            sender.getOutputStream().write(gson.toJson(response).getBytes());
        } catch (IOException ex) 
        {
            Logger.getLogger(AcceptRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean accept()
    {
        try {
            Connection connection = Database.getConnection();
            String status = (this.status)? "'aceptado'" : "'rechazado'";
            String query = "UPDATE amistad SET estado = "+status+" WHERE "
                    + "propietario = "+friend+" and amigo = "+owner;
             PreparedStatement select = connection.prepareStatement(query);
             select.execute();
            System.out.println(query);
            query = "INSERT INTO amistad(estado , amigo , propietario) VALUES "
                    + "("+status+","+friend+","+owner+") ON DUPLICATE KEY "
                    + " UPDATE estado = "+status+" ";
            select = connection.prepareStatement(query);
            select.execute();
            System.out.println(query);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AcceptRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
}

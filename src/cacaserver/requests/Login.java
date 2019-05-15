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
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class Login 
{ 
    private String username;
    private String password;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;
    
    static
    {
        logger = Logger.getLogger("Login");
    }
    
    public Login(JsonObject args, Socket sender, Context context)
    {
        this.sender = sender;
        this.context = context;
        this.username = args.get("username").getAsString();
        this.password = args.get("password").getAsString();
                
        try 
        {
            OutputStream out = sender.getOutputStream();
            JsonObject response = new JsonObject();
            response.addProperty("type", "login");
            
            JsonObject responseArgs = getArgs(getLogin());
            
            response.add("args", responseArgs);
            
            Gson gson = new Gson();
            
            String packet = gson.toJson(response);
            
            System.out.println(packet);
            
            out.write(packet.getBytes());
        } 
        catch (IOException ex) 
        {
            Database.returnConnection(connection);
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
    private JsonObject getArgs(boolean statusCode)
    {
        JsonObject response = new JsonObject();
        response.addProperty("status", statusCode);
        if(statusCode)
        {
            Hashtable connected = context.getConnectedUsers();
            synchronized(connected)
            {
                logger.info(username +" just logged in with IP "+sender.getInetAddress());
                connected.put(sender, username);
                connected.notify();
            }
            /**
             * Agregar listas.
             */
        }
        Hashtable connected = context.getConnectedUsers();
        JsonArray connectedList = new JsonArray();
        
        synchronized(connected)
        {
            connected.forEach((socket,username)->
            {
                JsonObject user = new JsonObject();
                user.addProperty("username",(String) username);
                connectedList.add(user);
            });
            connected.notify();
        }
        
        response.add("connected", connectedList);
        
        Database.returnConnection(connection);
        return response;
    }
    
    private boolean getLogin()
    {
        connection = Database.getConnection();
        
        try 
        {
            String query = "SELECT password FROM usuario WHERE username='"+username+"'";
            PreparedStatement select = connection.prepareStatement(query);
            ResultSet result = select.executeQuery();
            
            if(!result.next())
            {
                return false;
            }
            return new String(result.getString("password")).equals(password);
        }
        catch(SQLException  ex)
        {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }
}

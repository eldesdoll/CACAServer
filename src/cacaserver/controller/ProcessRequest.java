/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.controller;

import cacaserver.database.Database;
import cacaserver.tasker.TaskManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class ProcessRequest 
{
    private static JsonParser parser;
    private static Database db;
    private static Logger logger;
    
    static 
    {
        parser = new JsonParser();
        db = new Database();
    }
    
    public static String processRequest(String request)
    {
        JsonObject response = parser.parse(request).getAsJsonObject();
        switch(response.get("type").getAsString())
        {
            case "login":
                return getLogin(response.get("args").getAsJsonObject());
            default:
                break;
        }
        return "";
    }
    
    private static String getLogin(JsonObject args)
    {
        String username = args.get("username").getAsString();
        String password = args.get("password").getAsString();
        
        String pass=null, response = "";
        
        Connection connection = db.getConnection();
        
        try
        {
            String query = "SELECT password FROM usuario WHERE username='"+username+"'";
            PreparedStatement st = connection.prepareStatement(query);
            ResultSet result = st.executeQuery();
            
            
            if(!result.next())
            {
                response="not ok";
            }
            else 
            {
                pass = new String(result.getString("password"));
                if(pass.equals(password))
                {
                    response = "ok";
                    //Aquí se crean todos los métodos de obtener cosas ajjja;
                }
                else 
                {
                    response = "not ok";
                }
            }
        }
        catch(SQLException  ex)
        {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        
        db.unLock();
        return response;
    }
}
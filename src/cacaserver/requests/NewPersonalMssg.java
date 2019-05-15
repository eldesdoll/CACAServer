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
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manie
 */
public class NewPersonalMssg {
    private String remitente;
    private String destinatario;
    private String mssg;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;
    
    
    static
    {
        logger = Logger.getLogger("NewGroup");
    }
    
    public NewPersonalMssg(JsonObject args, Socket sender, Context context)
    {
       try {
            this.sender = sender;
            this.remitente = args.get("remitente").getAsString();
            this.destinatario = args.get("destinatario").getAsString();
            this.mssg = args.get("mssg").getAsString();
            connection = Database.getConnection();
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "newPersonalMssg");
            response.addProperty("status", newPerMssg());
            
            Login upd = new Login(context);
            response.add("args", upd.updateArgs(remitente));
            
            logger.info("New personal message registered with status code "+response.get("status").getAsBoolean());
            
            Gson gson = new Gson();
            
            String envio = gson.toJson(response);
            
            sender.getOutputStream().write(envio.getBytes());
            
            Database.returnConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage());
        }
    }
    
    
    private boolean newPerMssg()
    {
        connection = Database.getConnection();
        
        try 
        {
            
            String query;
            query = "INSERT INTO chatamigo(mensaje, remitente, destinaatario) VALUES ('"+mssg+"','"+remitente+"','"+destinatario+"')";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.execute();
            return true;
        }
        catch(SQLException  ex)
        {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }
}

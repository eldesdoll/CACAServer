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
public class NewGroup {
     private String username;
    private String gname;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;
    
    
    static
    {
        logger = Logger.getLogger("NewGroup");
    }
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta función recibe los datos enviados por el 
     * cliente y manda a llamar a la función newGroup
     * con estos datos para intentar realizar una 
     * inserción en la base de datos. La función 
     * prepara y envía una respuesta al usuario, 
     * marcando si la operación fue exitosa o no.
     */
    public NewGroup(JsonObject args, Socket sender, Context context)
    {
       try {
            this.sender = sender;
            this.username = args.get("admin").getAsString();
            this.gname = args.get("groupName").getAsString();
            connection = Database.getConnection();
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "newGroup");
            response.addProperty("status", newGroup());
            
            Login upd = new Login(context);
            response.add("args", upd.updateArgs(username));
            
            logger.info("New group registered with status code "+response.get("status").getAsBoolean());
            
            Gson gson = new Gson();
            
            String envio = gson.toJson(response);
            
            sender.getOutputStream().write(envio.getBytes());
            
            Database.returnConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage());
        }
    }
    
    /**
     * 
     * @return 
     * Esta clase intenta insertar en la base de datos 
     * un nuevo grupo con los datos enviados por el 
     * cliente. En caso de que la operación se llevó
     * a cabo sin errores, la función retorna 
     * verdadero, en caso contrario devuelve falso.
     */
    private boolean newGroup()
    {
        connection = Database.getConnection();
        
        try 
        {
            String query;
            query = "INSERT INTO grupo(asunto, administrador) VALUES ('"+gname+"','"+username+"')";
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

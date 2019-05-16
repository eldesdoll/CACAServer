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
public class AddToGroup 
{
    private String username;
    private String id;
    private Connection connection;
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion recibe los datos enviados por un cliente
     * e intenta insertar nuevos miembros en un grupo. La 
     * función prepara y envia una respuesta que marca con un
     * verdadero si la inserción fue exitosa o en caso contrario
     * con un falso.
     */
    public AddToGroup(JsonObject args, Socket sender, Context context)
    {
        try 
        {
            username = args.get("username").getAsString();
            id = args.get("id").getAsString();
            connection = Database.getConnection();
            
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "add-to-group");
            resp.addProperty("status",add());
            
            Login login = new Login(context);
            resp.add("args",login.updateArgs(username));
            
            Gson gson = new Gson();
            
            sender.getOutputStream().write(gson.toJson(resp).getBytes());
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(AddToGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private boolean add()
    {
        try {
            String query = "INSERT INTO miembro(usuario, grupo) VALUES "
                    + "('"+username+"',"+id+")";
            PreparedStatement insert = connection.prepareStatement(query);
            insert.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AddToGroup.class.getName()).log(Level.SEVERE, null, ex);            
            return false;
        }
    }
}

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manie
 */
public class ModifyGroup {

    private String username;
    private String gId;
    private String gName;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;
    
    static {
        logger = Logger.getLogger("DeleteGroup");
    }
    
    public ModifyGroup(JsonObject args, Socket sender, Context context) {
        try {
            this.sender = sender;
            this.username = args.get("admin").getAsString();
            this.gId = args.get("groupId").getAsString();
            this.gName = args.get("name").getAsString();
            connection = Database.getConnection();
            if (getAdmin().equals(username)) {
                JsonObject response = new JsonObject();
                response.addProperty("type", "modifyGroup");
                response.addProperty("status", modifyGroup());

                Login upd = new Login(context);
                response.add("args", upd.updateArgs(username));
                
                logger.info("The group was removed with status code " + response.get("status").getAsBoolean());
                
                Gson gson = new Gson();
                
                String envio = gson.toJson(response);
                
                sender.getOutputStream().write(envio.getBytes());
            }
            else 
            {
                JsonObject response = new JsonObject();
                response.addProperty("type", "deleteGroup");
                response.addProperty("status", false);
                Login upd = new Login(context);
                response.add("args", upd.updateArgs(username));
                sender.getOutputStream().write(new Gson().toJson(response).getBytes());
            }
            
            Database.returnConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
    private String getAdmin() {
        try {
            String admin = null;
            
            String query;
            query = "SELECT administrador FROM grupo WHERE id = "+gId;
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet result = stmt.executeQuery();
            if(result.next()){
                admin = result.getString("administrador");
            }
            
            return admin;
            
        } catch (SQLException ex) {
            Logger.getLogger(DeleteGroup.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
 
    }
    
    private boolean modifyGroup() {
        connection = Database.getConnection();
        
        try {
            
            String query;
            query = "UPDATE grupo SET asunto = '"+gName+"' WHERE id = "+gId;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }
}

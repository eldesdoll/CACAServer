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
public class DeleteGroup {

    private String username;
    private String gId;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;
    
    static {
        logger = Logger.getLogger("DeleteGroup");
    }
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta función se encarga de verificar que el usuario
     * que haya enviado la peticion de eliminación de grupo
     * sea el administrador del grupo. En caso de que este
     * lo sea, se intenta eliminar de la base de datos el 
     * grupo y se responde al cliente con el status. El status
     * será Verdadero si el campo se pudo eliminar o Falso en
     * caso contrario.
     */
    public DeleteGroup(JsonObject args, Socket sender, Context context) {
        try {
            this.sender = sender;
            this.username = args.get("admin").getAsString();
            this.gId = args.get("groupId").getAsString();
            connection = Database.getConnection();
            if (getAdmin().equals(username)) {
                JsonObject response = new JsonObject();
                response.addProperty("type", "deleteGroup");
                response.addProperty("status", deleteGroup());

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
    
    /**
     * Esta funcion consulta en la base de datos
     * el nombre del administrador del grupo que 
     * se está manipluando en la clase.
     * @return 
     * Regresa el nombre del administrador
     */
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
    
    
    /**
     * Esta funcion elimina en la base de datos
     * el grupo que se está manipuñando en la base.
     * @return 
     * En caso de que la función se pudo hacer con
     * exito, se regresa true, en caso contrario
     * se regresa false
     */
    private boolean deleteGroup() {
        connection = Database.getConnection();
        
        try {
            
            String query;
            query = "DELETE FROM grupo WHERE id = "+gId;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }
}

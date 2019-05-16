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
public class Sign 
{
    private Socket sender;
    private Context context;
    private String username;
    private String password;
    private Connection connection;
    private static Logger logger;
    
    static
    {
        logger = Logger.getLogger("Sign");
    }
    
    /**
     * 
     * @param args
     * @param sender 
     * Esta funcion recibe los datos enviados por
     * el cliente y llama la función register para
     * intentar hacer la creación del registro del 
     * usuario en la base de datos. Esta funcion
     * prepara y envia una respuesta la cual marca
     * como verdadero en caso de que la creación del
     * usuario fue exitosa y falsa en caso contrario.
     */
    public Sign(JsonObject args, Socket sender)
    {
        try {
            this.sender = sender;
            this.username = args.get("username").getAsString();
            this.password = args.get("password").getAsString();
            connection = Database.getConnection();
            
            JsonObject response = new JsonObject();
            response.addProperty("type", "sign");
            response.addProperty("status", register());
            
            logger.info("New user registered with status code "+response.get("status").getAsBoolean());
            
            Gson gson = new Gson();
            
            String envio = gson.toJson(response);
            
            sender.getOutputStream().write(envio.getBytes());
            
            Database.returnConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage());
        }
    }
    
    /**
     * Esta función intenta insertar en la base
     * de datos un nuevo registro de usuario con
     * los datos enviados por el cliente.
     * @return 
     * En caso de que la inserción a la base de 
     * datos fue exitosa, la función devuelve 
     * verdadero, y en caso contrario, devuelve
     * falso.
     */
    public boolean register()
    {
        try {
            String query = "INSERT INTO usuario(username,password) VALUES ('"+username+"', '"+password+"')";
            PreparedStatement st = connection.prepareStatement(query);
            st.execute();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }    
}
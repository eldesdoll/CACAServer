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
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion es usada para intentar aceptar o rechazar una solicitud
     * de amistad. Prepara y devulve una respuesta al cliente, diciendo si 
     * la operación fue exitosa o no.
     */
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
    
    /**
     * 
     * @return 
     * Esta funcion actualiza la base de datos y crea la amistad cuando
     * el cliente decidió aceptar la solicitud de amistad. En caso de que
     * el cliente haya decidido rechzarla, solamente de marca como rechazada.
     * La función devuelve verdadero en caso de que la operaciones con la base 
     * de datos fueron exitosas, o falso en el caso contrario.
     */
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

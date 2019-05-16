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
 * @author manie
 */
public class ModifyAlias {

    private String username;
    private String alias;
    private String friend;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;

    static {
        logger = Logger.getLogger("DeleteGroup");
    }

    public ModifyAlias(JsonObject args, Socket sender, Context context) {
        try {
            this.sender = sender;
            this.username = args.get("username").getAsString();
            this.friend = args.get("friend").getAsString();
            this.alias = args.get("alias").getAsString();
            connection = Database.getConnection();

            JsonObject response = new JsonObject();
            response.addProperty("type", "modifyAlias");
            response.addProperty("status", modifyAlias());

            Login upd = new Login(context);
            response.add("args", upd.updateArgs(username));

            logger.info("The alias was modified with status code " + response.get("status").getAsBoolean());

            Gson gson = new Gson();

            String envio = gson.toJson(response);

            sender.getOutputStream().write(envio.getBytes());

            Database.returnConnection(connection);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    private boolean modifyAlias() {
        connection = Database.getConnection();

        try {
            String query;
            query = "UPDATE amistad SET alias = '" + alias + "' WHERE propietario = '" + username + "' AND amigo = '" + friend + "'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }
}

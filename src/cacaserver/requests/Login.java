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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class Login {

    private String username;
    private String password;
    private static Logger logger;
    private Connection connection;
    private Context context;
    private Socket sender;

    static {
        logger = Logger.getLogger("Login");
    }
    
    /**
     * 
     * @param context 
     * Esta funcion obtiene el contexto de la clase
     */
    public Login(Context context) {
        this.context = context;
    }
    
    /**
     * 
     * @param username
     * @return 
     * 
     * Esta funcion actualiza los argumentos que se usaran
     * para crear la vista principal del programa
     */
    public JsonObject updateArgs(String username) 
    {
        connection = Database.getConnection();
        this.username = username;
        return getArgs();

    }
    
    /**
     * 
     * @param args
     * @param sender
     * @param context 
     * 
     * Esta funcion recibe los datos enviados por el cliente
     * e intenta hacer login con ellos. Esta funcion prepara
     * y envia al cliente todos los datos necesarios para
     * crear la vista principal del programa.
     */
    public Login(JsonObject args, Socket sender, Context context) {
        this.sender = sender;
        this.context = context;
        this.username = args.get("username").getAsString();
        this.password = args.get("password").getAsString();

        try {
            OutputStream out = sender.getOutputStream();
            JsonObject response = new JsonObject();
            response.addProperty("type", "login");

            JsonObject responseArgs = getArgs(getLogin());

            response.add("args", responseArgs);

            Gson gson = new Gson();

            String packet = gson.toJson(response);

            System.out.println(packet);

            out.write(packet.getBytes());
        } catch (IOException ex) {
            Database.returnConnection(connection);
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
    /**
     * 
     * @return 
     * Esta funcion obtiene los datos necesarios para crear el 
     * JsonObject que se usará para rellenar los paneles de la 
     * vista principal.
     */
    private JsonObject getArgs() 
    {
        JsonObject response = new JsonObject();
        Hashtable connected = context.getConnectedUsers();
        JsonArray connectedList = new JsonArray();
        JsonArray disconnectedList = new JsonArray();

        ArrayList<String> stringConnected = new ArrayList<>();
        ArrayList<String> stringDisconnected = getEveryUser();

        synchronized (connected) {
            connected.forEach((socket, username)
                    -> {
                JsonObject user = new JsonObject();
                user.addProperty("username", (String) username);
                connectedList.add(user);
                stringConnected.add((String) username);
            });
            connected.notify();
        }

        stringDisconnected.removeAll(stringConnected);
        stringDisconnected.forEach(username
                -> {
            JsonObject user = new JsonObject();
            user.addProperty("username", username);
            disconnectedList.add(user);
        });

        response.add("connected", connectedList);
        response.add("disconnected", disconnectedList);
        response.add("notifications", getNotifications());
        response.add("friends", getFriends());
        response.add("groups", getGroups());
        Database.returnConnection(connection);
        return response;
   }

    /**
     * 
     * @param statusCode
     * @return 
     * 
     * Esta funcion obtiene los datos necesarios para crear el 
     * JsonObject que se usará para rellenar los paneles de la 
     * vista principal.
     */
    private JsonObject getArgs(boolean statusCode) {
        JsonObject response = new JsonObject();
        response.addProperty("status", statusCode);
        if (statusCode) {
            Hashtable connected = context.getConnectedUsers();
            synchronized (connected) {
                logger.info(username + " just logged in with IP " + sender.getInetAddress());
                connected.put(sender, username);
                connected.notify();
            }
            JsonArray connectedList = new JsonArray();
            JsonArray disconnectedList = new JsonArray();

            ArrayList<String> stringConnected = new ArrayList<>();
            ArrayList<String> stringDisconnected = getEveryUser();

            synchronized (connected) {
                connected.forEach((socket, username)
                        -> {
                    JsonObject user = new JsonObject();
                    user.addProperty("username", (String) username);
                    connectedList.add(user);
                    stringConnected.add((String) username);
                });
                connected.notify();
            }

            stringDisconnected.removeAll(stringConnected);
            stringDisconnected.forEach(username
                    -> {
                JsonObject user = new JsonObject();
                user.addProperty("username", username);
                disconnectedList.add(user);
            });

            response.add("connected", connectedList);
            response.add("disconnected", disconnectedList);
            response.add("notifications", getNotifications());
            response.add("friends", getFriends());
            response.add("groups", getGroups());
            /**
             * Agregar listas.
             */
        }

        Database.returnConnection(connection);
        return response;
    }
    
    /**
     * Esta funcion consulta en la base de datos todos 
     * los usuarios que son amigos del cliente. Todos los
     * amigos se van añadiendo a una JsonArray.
     * @return 
     * Se devuelve el JsonArray que contiene todos los amigos
     * del cliente.
     */
    private JsonArray getFriends()
    {
        JsonArray friends = new JsonArray();
        try {
            String query  = "SELECT amigo,alias FROM amistad WHERE "
                    + "propietario = '"+username+"'"
                    + " AND estado='aceptado'";
            
            PreparedStatement select = connection.prepareCall(query);
            ResultSet result = select.executeQuery();
            while(result.next())
            {
                JsonObject amigo = new JsonObject();
                amigo.addProperty("username", result.getString("amigo"));
                amigo.addProperty("alias", result.getString("alias"));
                friends.add(amigo);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friends;
    }
    
    /**
     * Esta funcion consulta en la base de datos en que grupos el
     * cliente se encuentra y se agregan al un JsonArray.
     * @return 
     * La funcion devuelve el JsonArray que contiene todos los
     * grupos en los que el usuario se encuentra.
     */
    private JsonArray getGroups()
    {
        JsonArray groups = new JsonArray();
            try {
            String query  = "SELECT grupo.id as clave, grupo.asunto as nombre "
                    + " FROM grupo, miembro WHERE grupo.id = miembro.grupo AND"
                    + " miembro.usuario = '"+username+"' AND estado = 'aceptado'";
            PreparedStatement select = connection.prepareCall(query);
            ResultSet result = select.executeQuery();
            while(result.next())
            {
                JsonObject amigo = new JsonObject();
                amigo.addProperty("id", result.getString("clave"));
                amigo.addProperty("asunto", result.getString("nombre"));
                groups.add(amigo);
            }
    } catch (SQLException ex) {
        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
    }
        return groups;
    }

    /**
     * Esta funcion obtiene los nombres de todos los usuarios
     * que no sea el propio cliente.
     * @return 
     * La funcion devuelve un array list de Strings que contienen
     * los nombre de los usuarios.
     */
    private ArrayList<String> getEveryUser() {
        ArrayList<String> all = new ArrayList<>();
        try {
            String query = "SELECT * FROM usuario WHERE username!='" + username + "'";
            PreparedStatement select = connection.prepareStatement(query);
            ResultSet result = select.executeQuery();

            while (result.next()) {
                all.add(new String(result.getString("username")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
    
    /**
     * Esta funcion está encargada de hacer consultas a multiples
     * tablas de la base de datos para saber si tenemos notificaciones.
     * En caso de existir, se agregaran a un JsonArray.
     * @return 
     * La funcion devuelve el JsonArray con todas las notificaciones.
     */
    private JsonArray getNotifications() {
        JsonArray notifications = new JsonArray();
        try {
            String query = "SELECT amigo FROM amistad WHERE estado = 'pendiente' AND propietario = '" + username + "'";
            PreparedStatement select = connection.prepareStatement(query);
            ResultSet result = select.executeQuery();
            while (result.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("type", "friend-unnacepted");
                it.addProperty("origin", result.getString("amigo"));
                notifications.add(it);
            }
            query = "SELECT propietario FROM amistad WHERE estado = 'pendiente' AND amigo = '" + username + "'";
            select = connection.prepareStatement(query);
            result = select.executeQuery();
            while (result.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("type", "friend");
                it.addProperty("origin", result.getString("propietario"));
                notifications.add(it);
            }
            query = "SELECT grupo.asunto as arg, grupo.id as id FROM grupo, miembro "
                    + "WHERE grupo.id = miembro.grupo AND estado = 'invitado'"
                    + "AND usuario = '" + username + "'";
            select = connection.prepareStatement(query);
            result = select.executeQuery();
            while (result.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("type", "group");
                it.addProperty("origin", result.getString("arg"));
                it.addProperty("id", result.getString("id"));
                notifications.add(it);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        return notifications;
    }
    
    /**
     * Esta funcion esta encargada de comprobar si el nombre de usuario
     * que envio el cliente está dentro de la base de datos. En caso de
     * que si exista, se comprueba que la contraseña enviada por el 
     * cliente. Si la contraseña no coincide con la de la base de
     * datos, o el nombre de usuario no existe, se devuelve falso.
     * @return 
     */
    private boolean getLogin() {
        connection = Database.getConnection();

        try {
            String query = "SELECT password FROM usuario WHERE username='" + username + "'";
            PreparedStatement select = connection.prepareStatement(query);
            ResultSet result = select.executeQuery();

            if (!result.next()) {
                return false;
            }
            return new String(result.getString("password")).equals(password);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }
}

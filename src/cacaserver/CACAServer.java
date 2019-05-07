/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver;

import cacaserver.pojos.Login;
import cacaserver.pojos.Messages;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

/**
 *
 * @author ivan_
 */
public class CACAServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        /*
        //Prueba de envio de mensaje simple
        
        Messages send = new Messages();
        send.setType("login");
        Login arguments = new Login();
        arguments.setPassword("123");
        arguments.setUsername("ivxn");
        send.setArgs(arguments);
        
        Gson gson = new Gson();
        String msg = gson.toJson(send);
        
        System.out.println(msg);
        
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(msg).getAsJsonObject();
        
        System.out.println(response.get("type").getAsString());
        
        System.out.println(response.get("args").getAsJsonObject().get("username").getAsString());
        */
        
        JsonObject envio = new JsonObject();
        envio.addProperty("type", "connected-users");
        
        JsonArray list = new JsonArray();
        
        ArrayList<User> users = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) 
        {
            User user = new User("Ivan",""+i);
            users.add(user);
        }
        
        for (int i = 0; i < users.size(); i++) 
        { 
           JsonObject usr = new JsonObject();
           usr.addProperty("username", users.get(i).getUsername());
           usr.addProperty("alias", users.get(i).getAlias());
           list.add(usr);
        }
        
        envio.add("args", list);
        
        Gson gson = new Gson();
        String output = gson.toJson(envio);
        System.out.println(output);
        
        //Recepción
        
        JsonObject response =new JsonObject();
        
        JsonParser parser = new JsonParser();
        
        response = parser.parse(output).getAsJsonObject();
        
        if(response.get("type").getAsString().equals("connected-users"))
        {
            JsonArray responseList = new JsonArray();
            responseList = response.get("args").getAsJsonArray();
            ArrayList<User> userList = new ArrayList<>();
            responseList.forEach(u ->
            { 
                JsonObject user = u.getAsJsonObject();
                User cUser = new User(user.get("username").getAsString(),user.get("alias").getAsString());
                userList.add(cUser);
            });
            
            userList.forEach(u->
            {
                System.out.println("Usuario: "+u.getUsername()+" Alias: "+u.getAlias());
            });
        }
        
       
        
        
        
        /*
        //Creación
        Messages send = new Messages();
        send.setType("connected-users");
        ArrayList<User> usuarios = new ArrayList<>();
        for (int i = 0; i < 10; i++) 
        { 
            User user = new User("ivan","no ivan");
            usuarios.add(user);
        }
        
        send.setArgs(usuarios);
        
        Gson gson = new Gson();
        String msg = gson.toJson(send);
        
        System.out.println(""+msg);
        
        
        //Recepción
        
        Messages rcv = gson.fromJson(msg, Messages.class);
        
        ArrayList<Object> uss = gson.fromJson(rcv.getArgs().toString(), ArrayList.class);
        
        for (int i = 0; i < uss.size(); i++) {
            
        }
       
        System.out.println(rcv.getType());
        //En este punto se verifica type para saber qué hacer ajaj
        
        System.out.println(rcv.getArgs());
        ArrayList<User> uss = gson.fromJson(rcv.getArgs().toString(), new TypeToken<ArrayList<User>>(){}.getType());
        //UserList us = gson.fromJson(rcv.getArgs().toString(), UserList.class);
        
       /*
        for (int i = 0; i < 10; i++) 
        { 
            User u = gson.fromJson(us.toString(), User.class);
            System.out.println(u.getUsername());
            
        }*/
    }
    
}

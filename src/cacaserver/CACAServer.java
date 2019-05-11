/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver;

/**
 * Clase ejecutable, solo instancia al servidor
 * @author ivan_
 */
public class CACAServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server(5000);

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

    /*JsonObject envio = new JsonObject();
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
    }*/
    }
}
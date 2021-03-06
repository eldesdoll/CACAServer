/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author ivan_
 */
public class Context 
{ 
    private ServerSocket server;
    private ArrayList<Socket> clients;
    private Hashtable<Socket, String> connectedUsers;
    
    /**
     * Este objeto mantiene todos los objetos necesarios
     * para el server, de esta forma se tiene un acceso rápido
     * a recursos declarados entre clases
     * @param server Socket usado para correr el servidor
     * @param clients Lista de clientes conectados
     * @param connectedUsers  Lista de usuarios conectados
     */
    public Context(ServerSocket server, ArrayList<Socket> clients, Hashtable<Socket, String> connectedUsers)
    {
        this.server = server;
        this.clients = clients;
        this.connectedUsers = connectedUsers;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public ArrayList<Socket> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Socket> clients) {
        this.clients = clients;
    }

    public Hashtable<Socket, String> getConnectedUsers() {
        return connectedUsers;
    }
    
    /**
     * 
     * @param connectedUsers 
     */
    public void setConnectedUsers(Hashtable<Socket, String> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }
    
    /**
     * 
     * @param user
     * @return 
     * 
     * Esta funcion devuelve la conexión que 
     * coincida con el nombre que recibió como 
     * parametro
     */
    public ArrayList<Socket> getSocketsByUsername(String user)
    {
        ArrayList<Socket> connectedOn = new ArrayList<>();
        synchronized(connectedUsers)
        {
            connectedUsers.forEach((socket, username) -> 
            {
               if(username.equals(user))
               {
                   connectedOn.add(socket);
               }
            });
            connectedUsers.notify();
        }
        return connectedOn;
    }
}

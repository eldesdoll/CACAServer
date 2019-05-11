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

    public void setConnectedUsers(Hashtable<Socket, String> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }
    
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

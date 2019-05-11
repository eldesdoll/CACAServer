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
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class Context 
{ 
    private ServerSocket server;
    private ArrayList<Socket> clients;
    private Hashtable<String, Socket> connectedUsers;
    
    public Context(ServerSocket server, ArrayList<Socket> clients, Hashtable<String, Socket> connectedUsers)
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

    public Hashtable<String, Socket> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(Hashtable<String, Socket> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }
}

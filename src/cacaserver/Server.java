/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver;

import cacaserver.controller.Context;
import cacaserver.controller.ProcessRequest;
import cacaserver.tasker.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan_
 */
public class Server 
{
    /**
     * @param server Socket que corre el server
     * @param clients Lista de clientes conectados
     * @param logger Captura las excepciones
     */
    private ServerSocket server;
    private ArrayList<Socket> clients;
    private Logger logger;
    private Hashtable<Socket, String> connectedUsers;
    private Context context;
    
    /**
     * Constructor que inicializa el servidor
     * Inicializa las listas de sockets para clientes,
     * el servidor en sí y el logger.
     * Encola al TaskMannager las tareas de recibir mensajes,
     * aceptar conexiones y verificar constantemente qué usuarios
     * se han desconectado.
     * @param port Puerto que usa el servidor
     */
    Server(int port) //La excepcion se envía quien invocó el método (main)
    {
        clients = new ArrayList<>();
        connectedUsers = new Hashtable<>();
        logger = Logger.getLogger("Server");
        try 
        {
            server = new ServerSocket(port);
        }
        catch (IOException ex) 
        {
            logger.log(Level.SEVERE,  ex.getMessage());
            return;
        }
        TaskManager.equeue(this::getData);
        TaskManager.equeue(this::deleteDeads);
        TaskManager.equeue(this::getConnections);
        
        context = new Context(server, clients, connectedUsers);
    }
    
    /**
     * Este método se agrega al taskmannager para correr infinitamente,
     * llama al procesador de request como otra task
     */
    public void getData()
    {
        while(true)
        {
            synchronized(clients) //Acceso seguro
            {
                for (int i = 0; i < clients.size(); i++) 
                {
                    Socket current = clients.get(i);
                    try 
                    {
                        InputStream in = current.getInputStream();
                        int size = in.available();
                        if(size>0)
                        {
                            byte data[] = new byte[size];
                            in.read(data);
                            TaskManager.equeue(() -> 
                            {
                                ProcessRequest.processRequest(new String(data),current,context);
                            });                               
                        }
                    } 
                    catch (IOException ex) 
                    {
                        logger.log(Level.SEVERE, ex.getMessage());
                    }
                }
                clients.notify();
            }
        }
    }
    
    /**
     * Esta función se llama periodicamente para confirmar que
     * aún sigan con conexión los usuarios que están conectados,
     * si no lo están los borra como recurso y actualiza
     */
    public void deleteDeads()
    {
        synchronized(clients) //Se maneja de forma sincronizada para evitar fallas
        {
            ArrayList<Socket> deads = new ArrayList<>();
            for(int i=0; i<clients.size(); i++)
            {
                Socket current = clients.get(i);
                try
                {
                    OutputStream out = current.getOutputStream();
                    out.write("p".getBytes());
                    out.flush();
                }
                catch(IOException ex)
                {
                    deads.add(current);
                    logger.info(current.getInetAddress()+" has left the room");
                }
            }
            synchronized(connectedUsers)
            {
                ArrayList<Socket> toKill = new ArrayList<>();
                connectedUsers.forEach((Csocket, username)->
                {
                    if(deads.contains(Csocket))
                    {
                        logger.info(username+" has disconnected :(");
                        toKill.add(Csocket);
                    }
                });
                toKill.forEach(kSocket ->
                {
                    connectedUsers.remove(kSocket);
                });
                connectedUsers.notify();
            }
            clients.removeAll(deads);
            clients.notify();
        }
        try
        {
            Thread.sleep(2000);
            TaskManager.equeue(this::deleteDeads);
        } 
        catch (InterruptedException ex) 
        {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
    /**
     * Este método esta corriendo infinitamente
     * y es el encargado de recibir las conexiones que se vayan haciendo
     */
    public void getConnections()
    {
        while(true)
        {
            try 
            {
                Socket newClient = server.accept();
                synchronized(clients) //Se hace de forma sincronizada para evitar fallas
                {
                    clients.add(newClient);
                    clients.notify();
                }
                logger.info("New client connected with IP: " + newClient.getInetAddress());
            } 
            catch (IOException ex) 
            {
                logger.log(Level.SEVERE, ex.getMessage());
            }    
        }        
    }
}
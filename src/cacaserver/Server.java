/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver;

import cacaserver.tasker.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
        
    }
    
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
                            /**
                             * AQUÍ VA TODO EL PROCESAMIENTO 
                             * DE PETICIONES, AQUÍ SE CONVIERTE A
                             * JSON Y LLAMAN A LAS FUNCIONES CORRESPONDIENTES
                             */
                            System.out.println(new String(data));
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
                    out.write(0);
                    out.flush();
                }
                catch(IOException ex)
                {
                    deads.add(current);
                    logger.info(current.getInetAddress()+" has left the room");
                }
            }
            clients.removeAll(deads);
            clients.notify();
        }
        try 
        {
            Thread.sleep(100);
            TaskManager.equeue(this::deleteDeads);
        } 
        catch (InterruptedException ex) 
        {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
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
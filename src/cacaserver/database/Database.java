/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ivan_
 */
public class Database 
{ 
    private static ConcurrentLinkedDeque<Connection> pool;
    private static Logger logger;
    private static String database = "cacabase";
    private static String user = "root";
    private static String password = "";
    
    static
    {
        logger = Logger.getLogger("Database");
        pool = new ConcurrentLinkedDeque<>();
        for (int i = 0; i < 3; i++)          
        {
            try //Al inicio hay 3 conexiones en la pool
            {
                Class.forName("com.mysql.jdbc.Driver"); 
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/"+database,user,password);
                pool.add(connection);
            } 
            catch (ClassNotFoundException | SQLException ex) 
            {
                logger.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
    
    public static Connection getConnection()
    {
        if(pool.isEmpty())//No hay conexiones disponible
        {
            try 
            {
                Connection newConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+database,user,password);
                logger.info("Thread "+Thread.currentThread().getId()+" requested a new connection");
                return newConnection;
            } 
            catch (SQLException ex) 
            {
                logger.log(Level.SEVERE,ex.getMessage());
                return null;
            }
        }
        else 
        {
            return pool.getFirst();
        }
    }
    
    public static void returnConnection(Connection connection)
    {
        logger.info("Thread "+Thread.currentThread().getId()+" returned a connection");
        pool.addLast(connection);
    }
}

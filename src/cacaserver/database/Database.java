/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ivan_
 */
public class Database 
{ 
    private ReentrantLock lock = new ReentrantLock();
    static private Connection connection;
    
    static 
    {
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cacabase","root","");
        }
        catch (ClassNotFoundException | SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public Connection getConnection()
    {
        synchronized(connection)
        { 
            return lock.tryLock() ? this.connection : null;
        } 
    }
    
    public boolean unLock()
    {
        if(this.lock.isHeldByCurrentThread()){
            this.lock.unlock();
            return true;
        }
        return false;
    }
}

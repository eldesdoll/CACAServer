/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author ivan_
 */
public class Database 
{ 
    private Lock lock;
    static private Connection connection;
    
    static 
    { 
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/paises","root","");
        }
        catch (ClassNotFoundException | SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    } 
}

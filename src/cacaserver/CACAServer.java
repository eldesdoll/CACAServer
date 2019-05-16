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
public class CACAServer 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Server server = new Server(1000);
    }
}
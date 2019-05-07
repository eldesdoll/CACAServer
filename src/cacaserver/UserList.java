/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver;

import java.util.ArrayList;

/**
 *
 * @author ivan_
 */
public class UserList 
{
    private ArrayList<User> users;
    
    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    
    public UserList(ArrayList<User> users)
    {
        this.users = users;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cacaserver.pojos;

/**
 *
 * @author ivan_
 */
public class User 
{ 
    private String username;
    private String alias;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public User(String username, String alias)
    {
        this.username = username;
        this.alias = alias;
    }
    
}

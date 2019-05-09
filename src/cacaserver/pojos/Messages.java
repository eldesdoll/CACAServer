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
public class Messages 
{

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }
    private String type;
    private Object args;
}

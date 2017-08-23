/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.game;

/**
 *
 * @author Robert
 */
public class Weapon {
    
    private String UID;
    private int id;
    private int curr_health;
    private int equipped;

    public Weapon(String UID, int id, int curr_health, int equipped) {
        this.UID = UID;
        this.id = id;
        this.curr_health = curr_health;
        this.equipped = equipped;
    }

    public String getUID() {
        return UID;
    }

    public int getId() {
        return id;
    }

    public int getCurr_health() {
        return curr_health;
    }

    public int getEquipped() {
        return equipped;
    }
    
    
}

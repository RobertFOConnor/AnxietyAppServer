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
public class Item {
    
    private int id;
    private int quantity;

    public Item(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }
}

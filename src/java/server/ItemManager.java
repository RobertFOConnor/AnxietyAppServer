/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import static server.UpdateTables.URL;
import server.game.Item;

/**
 *
 * @author Robert
 */
public class ItemManager {

    public static void updateItems(ArrayList<Item> items, String playerId) {
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(URL, "root", null);
            PreparedStatement ps;

            for (Item item : items) {
                String itemId = String.valueOf(item.getId());
                int quantity = item.getQuantity();

                if (itemExists(playerId, itemId)) {
                    if (quantity > 0) {
                        ps = con.prepareStatement("UPDATE INVENTORY SET QUANTITY=? WHERE PLAYER_ID=? AND ITEM_ID=?");
                        ps.setInt(1, quantity);
                        ps.setString(2, playerId);
                        ps.setInt(3, Integer.parseInt(itemId));
                        ps.executeUpdate();
                    } else {
                        ps = con.prepareStatement("DELETE FROM INVENTORY WHERE PLAYER_ID=? AND ITEM_ID=?");
                        ps.setString(1, playerId);
                        ps.setInt(2, Integer.parseInt(itemId));
                    }

                } else {
                    ps = con.prepareStatement("INSERT INTO INVENTORY(PLAYER_ID, ITEM_ID, QUANTITY) values(?,?,?);");
                    ps.setString(1, playerId);
                    ps.setString(2, itemId);
                    ps.setString(3, String.valueOf(quantity));
                }
                ps.executeUpdate();
            }

            ps = con.prepareStatement("select * from INVENTORY where PLAYER_ID=?");
            ps.setString(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rs.getRow();
                int storedItemID = rs.getInt("ITEM_ID");

                boolean shouldDeleteStoredItem = true;
                for (Item item : items) {
                    if (item.getId() == storedItemID) {
                        shouldDeleteStoredItem = false;
                    }
                }

                if (shouldDeleteStoredItem) {
                    ps = con.prepareStatement("DELETE FROM INVENTORY WHERE PLAYER_ID=? AND ITEM_ID=?");
                    ps.setString(1, playerId);
                    ps.setInt(2, storedItemID);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean itemExists(String player_id, String item_id) {
        boolean status = false;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from INVENTORY where PLAYER_ID=? AND ITEM_ID=?");
            ps.setString(1, player_id);
            ps.setString(2, item_id);
            ResultSet rs = ps.executeQuery();
            status = rs.next();

        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }

}

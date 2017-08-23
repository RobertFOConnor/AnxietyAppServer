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
import server.game.Chest;

/**
 *
 * @author Robert
 */
public class ChestManager {
    public static void updateChests(ArrayList<Chest> chests, String playerId) {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(UpdateTables.URL, "root", null);
            PreparedStatement ps;

            for (Chest chest : chests) {
                String UID = chest.getUID();

                if (chestExists(playerId, UID)) {
                    ps = con.prepareStatement("UPDATE CHESTS SET DISTANCE_LEFT=? WHERE PLAYER_ID=? AND UID=?");
                    ps.setFloat(1, chest.getDistance());
                    ps.setString(2, playerId);
                    ps.setString(3, UID);

                } else {
                    ps = con.prepareStatement("INSERT INTO CHESTS(PLAYER_ID, UID, ID, DISTANCE_LEFT) values(?,?,?,?);");
                    ps.setString(1, playerId);
                    ps.setString(2, UID);
                    ps.setInt(3, chest.getItemID());
                    ps.setFloat(4, chest.getDistance());
                }
                ps.executeUpdate();
            }

            ps = con.prepareStatement("select * from CHESTS where PLAYER_ID=?");
            ps.setString(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rs.getRow();
                String storedChestUID = rs.getString("UID");

                boolean shouldDeleteStoredChest = true;
                for (Chest chest : chests) {
                    if (chest.getUID().equals(storedChestUID)) {
                        shouldDeleteStoredChest = false;
                    }
                }

                if (shouldDeleteStoredChest) {
                    ps = con.prepareStatement("DELETE FROM CHESTS WHERE PLAYER_ID=? AND UID=?");
                    ps.setString(1, playerId);
                    ps.setString(2, storedChestUID);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean chestExists(String player_id, String uid) {
        boolean status = false;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(UpdateTables.URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from CHESTS where PLAYER_ID=? AND UID=?");
            ps.setString(1, player_id);
            ps.setString(2, uid);
            ResultSet rs = ps.executeQuery();
            status = rs.next();

        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }
   
}

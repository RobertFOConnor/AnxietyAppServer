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
import server.game.Weapon;

/**
 *
 * @author Robert
 */
public class WeaponManager {

    public static void updateWeapons(ArrayList<Weapon> weapons, String playerId) {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(UpdateTables.URL, "root", null);
            PreparedStatement ps;

            for (Weapon weapon : weapons) {
                String UID = weapon.getUID();
                int currHealth = weapon.getCurr_health();

                if (weaponExists(playerId, UID)) {
                    ps = con.prepareStatement("UPDATE WEAPONS SET CURR_HEALTH=?, EQUIPPED=? WHERE WEAPON_UID=?");
                    ps.setInt(1, currHealth);
                    ps.setInt(2, weapon.getEquipped());
                    ps.setString(3, UID);

                } else {
                    ps = con.prepareStatement("INSERT INTO WEAPONS(PLAYER_ID, WEAPON_UID, WEAPON_ID, CURR_HEALTH, EQUIPPED) values(?,?,?,?,?);");
                    ps.setString(1, playerId);
                    ps.setString(2, UID);
                    ps.setInt(3, weapon.getId());
                    ps.setInt(4, currHealth);
                    ps.setInt(5, weapon.getEquipped());
                    System.out.println("Weapon added!!!");
                }
                ps.executeUpdate();
            }

            ps = con.prepareStatement("select * from WEAPONS where PLAYER_ID=?");
            ps.setString(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rs.getRow();
                String storedWeaponUID = rs.getString("WEAPON_UID");

                boolean shouldDeleteStoredWeapon = true;
                for (Weapon weapon : weapons) {
                    if (weapon.getUID().equals(storedWeaponUID)) {
                        shouldDeleteStoredWeapon = false;
                    }
                }

                if (shouldDeleteStoredWeapon) {
                    ps = con.prepareStatement("DELETE FROM WEAPONS WHERE PLAYER_ID=? AND WEAPON_UID=?");
                    ps.setString(1, playerId);
                    ps.setString(2, storedWeaponUID);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean weaponExists(String player_id, String uid) {
        boolean status = false;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(UpdateTables.URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from WEAPONS where PLAYER_ID=? AND WEAPON_UID=?");
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

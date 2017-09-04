/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.game.Chest;
import server.game.Item;
import server.game.Location;
import server.game.Player;
import server.game.SurveyAnswer;
import server.game.Weapon;

/**
 *
 * @author Robert
 */
@WebServlet(name = "UpdateTables", urlPatterns = {"/servlet/UpdateTables"})
public class UpdateTables extends HttpServlet {

    public static final String URL = "jdbc:mysql://localhost:3306/socialanxietydb";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        Player player = getPlayer(request);
        ArrayList<Item> items = getItems(request);
        ArrayList<Weapon> weapons = getWeapons(request);
        ArrayList<Location> locations = getLocations(request);

        ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, "root", null);
            PreparedStatement ps;

            if (playerExists(player.getId())) {
                ps = con.prepareStatement("UPDATE PLAYERS SET NAME=?, LEVEL=?, XP=?, MONEY=?, MAX_HEALTH=?, CURR_HEALTH=? WHERE ID=?");
                ps.setString(1, player.getName());
                ps.setInt(2, player.getLevel());
                ps.setInt(3, player.getXp());
                ps.setInt(4, player.getMoney());
                ps.setInt(5, player.getMaxHealth());
                ps.setInt(6, player.getCurrHealth());
                ps.setString(7, player.getId());
                ps.executeUpdate();

            } else {
                ps = con.prepareStatement("INSERT INTO PLAYERS(ID, NAME, LEVEL, XP, MONEY, MAX_HEALTH, CURR_HEALTH, BASE_SWORD) values(?,?,?,?,?,?,?,?);");
                ps.setString(1, player.getId());
                ps.setString(2, player.getName());
                ps.setInt(3, player.getLevel());
                ps.setInt(4, player.getXp());
                ps.setInt(5, player.getMoney());
                ps.setInt(6, player.getMaxHealth());
                ps.setInt(7, player.getCurrHealth());
                ps.setString(8, player.getBaseSword());
            }
            ps.executeUpdate();

            ItemManager.updateItems(items, player.getId());
            WeaponManager.updateWeapons(weapons, player.getId());
            ChestManager.updateChests(getChests(request), player.getId());

            for (Location location : locations) {
                String playerId = player.getId();

                if (locationExists(playerId, (float) location.getLat(), (float) location.getLng())) {
                    ps = con.prepareStatement("UPDATE LOCATIONS SET TIME_OF_VISIT=? WHERE PLAYER_ID=? AND LAT=? AND LNG=?");
                    ps.setLong(1, location.getTimeUsed());
                    ps.setString(2, playerId);
                    ps.setFloat(3, (float) location.getLat());
                    ps.setFloat(4, (float) location.getLng());

                } else {
                    ps = con.prepareStatement("INSERT INTO LOCATIONS(PLAYER_ID, LAT, LNG, TYPE, TIME_OF_VISIT) values(?,?,?,?,?);");
                    ps.setString(1, playerId);
                    ps.setFloat(2, (float) location.getLat());
                    ps.setFloat(3, (float) location.getLng());
                    ps.setInt(4, location.getType());
                    ps.setLong(5, location.getTimeUsed());
                }
                ps.executeUpdate();
            }
            
            for (SurveyAnswer answer : getSurveyAnswers(request)) {
                String playerId = player.getId();

                if (!answerExists(playerId, answer.getQuestion(), answer.getDate())) {
                    ps = con.prepareStatement("INSERT INTO SURVEY(PLAYER_ID, QUESTION, ANSWER, DATE) values(?,?,?);");
                    ps.setString(1, playerId);
                    ps.setInt(2, answer.getQuestion());
                    ps.setInt(3, answer.getAnswer());
                    ps.setString(4, answer.getDate());
                }
                ps.executeUpdate();
            }
            
            
            con.close();
            out.writeObject("Player updated!");
            System.out.println("Player " + player.getName() + " has been updated! With " + items.size() + " items, " + weapons.size() + " weapons, " + locations.size() + " locations. time:"+new Timestamp(System.currentTimeMillis()).toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.writeObject("Connection failed.");
        }
        out.close();
    }

    public static boolean playerExists(String player_id) {
        boolean status = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from PLAYERS where ID=?");
            ps.setString(1, player_id);
            ResultSet rs = ps.executeQuery();
            status = rs.next();

        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }

    public static boolean locationExists(String player_id, float lat, float lng) {
        boolean status = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from LOCATIONS where PLAYER_ID=? AND LAT=? AND LNG=?");
            ps.setString(1, player_id);
            ps.setFloat(2, lat);
            ps.setFloat(3, lng);
            ResultSet rs = ps.executeQuery();
            status = rs.next();

        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }
    
    public static boolean answerExists(String player_id, int question, String date) {
        boolean status = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from SURVEY where PLAYER_ID=? AND QUESTION=? AND DATE=?");
            ps.setString(1, player_id);
            ps.setInt(2, question);
            ps.setString(3, date);
            ResultSet rs = ps.executeQuery();
            status = rs.next();

        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Player getPlayer(HttpServletRequest request) {
        return new Player(request.getParameter("id"),
                request.getParameter("name"),
                Integer.parseInt(request.getParameter("level")),
                Integer.parseInt(request.getParameter("xp")),
                Integer.parseInt(request.getParameter("money")),
                Integer.parseInt(request.getParameter("max_health")),
                Integer.parseInt(request.getParameter("curr_health")),
                request.getParameter("sword_id"));
    }

    private ArrayList<Item> getItems(HttpServletRequest request) {
        ArrayList<Item> items = new ArrayList();
        int count = 0;
        while (request.getParameter("item_id_" + count) != null) {
            items.add(new Item(Integer.parseInt(request.getParameter("item_id_" + count)), Integer.parseInt(request.getParameter("quantity_" + count))));
            count++;
        }
        return items;
    }

    private ArrayList<Weapon> getWeapons(HttpServletRequest request) {
        ArrayList<Weapon> weapons = new ArrayList();
        int count = 0;
        while (request.getParameter("weapon_uid_" + count) != null) {
            weapons.add(new Weapon(request.getParameter("weapon_uid_" + count),
                    Integer.parseInt(request.getParameter("weapon_id_" + count)),
                    Integer.parseInt(request.getParameter("weapon_curr_health_" + count)),
                    Integer.parseInt(request.getParameter("weapon_equipped_" + count))));
            count++;
        }
        return weapons;
    }

    private ArrayList<Location> getLocations(HttpServletRequest request) {
        ArrayList<Location> locations = new ArrayList();
        int count = 0;
        while (request.getParameter("location_lat_" + count) != null) {
            locations.add(new Location(Double.parseDouble(request.getParameter("location_lat_" + count)),
                    Double.parseDouble(request.getParameter("location_lng_" + count)),
                    Integer.parseInt(request.getParameter("location_type_" + count)),
                    Long.parseLong(request.getParameter("location_time_" + count))));
            count++;
        }
        return locations;
    }
    
    private ArrayList<Chest> getChests(HttpServletRequest request) {
        ArrayList<Chest> chests = new ArrayList();
        int count = 0;
        while (request.getParameter("chest_uid_" + count) != null) {
            chests.add(new Chest(request.getParameter("chest_uid_" + count),
                    Integer.parseInt(request.getParameter("chest_id_" + count)),
                    Float.parseFloat(request.getParameter("chest_distance_left_" + count))));
            count++;
        }
        return chests;
    }

    private ArrayList<SurveyAnswer> getSurveyAnswers(HttpServletRequest request) {
        ArrayList<SurveyAnswer> answers = new ArrayList();
        int count = 0;
        while (request.getParameter("survey_answer_" + count) != null) {
            answers.add(new SurveyAnswer(
                    Integer.parseInt(request.getParameter("survey_question_" + count)),
                    Integer.parseInt(request.getParameter("survey_answer_" + count)),
                    request.getParameter("survey_date_" + count)));
            count++;
        }
        return answers;
    }
    
}

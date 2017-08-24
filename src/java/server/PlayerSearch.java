/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.game.Player;

/**
 *
 * @author Robert
 */
@WebServlet(name = "PlayerInfo", urlPatterns = {"/servlet/PlayerSearch"})
public class PlayerSearch extends HttpServlet {

    private static final String URL = "jdbc:mysql://localhost:3306/socialanxietydb";
    private ResultSet rs;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String s1 = request.getParameter("name");
        
        ServletOutputStream out = response.getOutputStream();

        if (validate(s1)) {
            
            Player player = getPlayer();

            try {
                String str = "{ \"id\": \"" + rs.getString("ID") + "\", \"name\": \"" + rs.getString("NAME") + "\" }";
               
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Player Stats</title>");   
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+ request.getContextPath() +"/css/playerSearch.css\"/>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>"+player.getName()+"</h1>");
            out.println("<p>Level: "+player.getLevel()+"</p>");
            out.println("<p>Xp: "+player.getXp()+"</p>");
            out.println("<p>HP: "+player.getCurrHealth()+"/"+player.getMaxHealth()+"</p>");
            out.println("<p>Money: $"+player.getMoney()+"</p>");
            out.println("</body>");
            out.println("</html>");
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                out.write("Player could not be found or doesn't exist.".getBytes());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        out.flush();
        out.close();
    }

    public boolean validate(String name) {
        boolean status = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from PLAYERS where NAME=?");
            ps.setString(1, name);
            rs = ps.executeQuery();
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

    private Player getPlayer() {
        try {
        return new Player(rs.getString("ID"),
                rs.getString("NAME"),
                rs.getInt("LEVEL"),
                rs.getInt("XP"),
                rs.getInt("MONEY"),
                rs.getInt("MAX_HEALTH"),
                rs.getInt("CURR_HEALTH"),
                rs.getString("BASE_SWORD"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

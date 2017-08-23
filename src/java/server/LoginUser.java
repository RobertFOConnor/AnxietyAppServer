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
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Robert
 */
@WebServlet(name = "LoginUser", urlPatterns = {"/servlet/LoginUser"})
public class LoginUser extends HttpServlet {

    private static final String URL = "jdbc:mysql://localhost:3306/socialanxietydb";
    private ResultSet rs;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String s1 = request.getParameter("email");
        String s2 = request.getParameter("password");

        ServletOutputStream out = response.getOutputStream();

        if (validate(s1, s2)) {

            try {
                String str = "{ \"id\": \"" + rs.getString("ID") + "\", \"name\": \"" + rs.getString("NAME") + "\" }";
                out.write(str.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.flush();
        out.close();
    }

    public boolean validate(String email, String password) {
        boolean status = false;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(URL, "root", null);

            PreparedStatement ps = con.prepareStatement("select * from USERS where EMAIL=? and PASSWORD=?");
            ps.setString(1, email);
            ps.setString(2, password);
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
}

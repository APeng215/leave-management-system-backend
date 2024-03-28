package mypackage;

import com.google.gson.Gson;
import mypackage.util.DBHelper;
import mypackage.util.LoginResponse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginHandler extends HttpServlet {
    Connection connection;
    Gson gson;

    @Override
    public void init() {
        connection = DBHelper.getConnection();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        resp.setHeader("Access-Control-Allow-Origin", getServletContext().getInitParameter("frontIp"));
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // If username is null, means that the frontend is trying to log in automatically
        if (username.equals("")) {
            if (session.getAttribute("username") != null) {
                respondSuccess(req, resp);
            }
        }
        // If not, means that the frontend is logging in through password
        else if (verifyPassword(username, password)) {
            session.setAttribute("username", username);
            session.setMaxInactiveInterval(30 * 60);
            respondSuccess(req, resp);
        } else respondFailure(req, resp);

    }


    private boolean verifyPassword(String username, String password) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Account WITH (TABLOCK) WHERE 账号='" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next() && resultSet.getString("密码").equals(password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void respondSuccess(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Log in");
        resp.setContentType("application/json");
        HttpSession session = req.getSession();
        LoginResponse loginResponse = new LoginResponse(true, (String) session.getAttribute("username"));
        try {
            PrintWriter writer = resp.getWriter();
            writer.print(gson.toJson(loginResponse));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void respondFailure(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        LoginResponse loginResponse = new LoginResponse(false, null);
        try {
            PrintWriter writer = resp.getWriter();
            writer.print(gson.toJson(loginResponse));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

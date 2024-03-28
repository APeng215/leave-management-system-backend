package mypackage;

import com.google.gson.Gson;
import mypackage.util.DBHelper;
import mypackage.util.IDExpirationHelper;
import mypackage.util.UserInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Preparation
        resp.setHeader("Access-Control-Allow-Origin", getServletContext().getInitParameter("frontIp"));
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        // Verify the certificate
        if (req.getSession().getAttribute("username") == null) {
            IDExpirationHelper.respondIDExpiration(resp);
            return;
        }

        // Serve
        try {
            String username = (String) req.getSession().getAttribute("username");
            String sql = "SELECT * FROM PersonalInfo WITH (TABLOCK) WHERE [学号/工号] = " + username;
            ResultSet resultSet = DBHelper.executeQuery(sql);
            if (resultSet.next()) {
                PrintWriter writer = resp.getWriter();
                writer.print(new Gson().toJson(new UserInfo(
                        resultSet.getString(3),
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6)
                )));
            }

        } catch (SQLException e) {
            System.out.println("Occur an exception when selecting personal information:");
            e.printStackTrace();
        }
    }
}

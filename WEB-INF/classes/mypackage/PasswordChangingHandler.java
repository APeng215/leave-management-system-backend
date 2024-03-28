package mypackage;

import mypackage.util.DBHelper;
import mypackage.util.IDExpirationHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordChangingHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Preparation
        resp.setHeader("Access-Control-Allow-Origin", getServletContext().getInitParameter("frontIp"));
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        // Verify the certificate
        if (req.getSession().getAttribute("username") == null) {
            IDExpirationHelper.respondIDExpiration(resp);
        }

        try {
            String oldPassword = req.getParameter("oldPassword");
            String newPassword = req.getParameter("newPassword");
            PrintWriter writer = resp.getWriter();
            if (!checkEmpty(newPassword)) {
                writer.print("{\"changeSucceed\": false, \"reason\": \"新密码为空\"}");
                return;
            }
            if (!checkOldPassword(oldPassword, req)) {
                writer.print("{\"changeSucceed\": false, \"reason\": \"旧密码错误\"}");
                return;
            }
            changePassword(newPassword, req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changePassword(String newPassword, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        DBHelper.executeUpdate("UPDATE Account SET 密码 = " + newPassword + " " + "WHERE 账号 = " + req.getSession().getAttribute("username"));
        req.getSession().invalidate();
        PrintWriter writer = resp.getWriter();
        writer.print("{\"changeSucceed\": true}");
    }

    private boolean checkOldPassword(String oldPassword, HttpServletRequest req) throws SQLException {
        ResultSet resultSet = DBHelper.executeQuery("SELECT 密码 FROM Account WITH (TABLOCK) WHERE 账号 = " + req.getSession().getAttribute("username"));
        if(resultSet.next()){
            String originalPassword = resultSet.getString(1);
            return oldPassword.equals(originalPassword);
        }
        return false;
    }

    private boolean checkEmpty(String newPassword) {
        return !newPassword.equals("");
    }
}

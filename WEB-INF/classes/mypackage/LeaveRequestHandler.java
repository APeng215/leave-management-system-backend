package mypackage;

import com.google.gson.Gson;
import mypackage.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class LeaveRequestHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", getServletContext().getInitParameter("frontIp"));
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        // Verify the session
        if (session.getAttribute("username") == null) {
            IDExpirationHelper.respondIDExpiration(resp);
            return;
        }

        LeaveRequest leaveRequest = LeaveRequest.ofRequest(req);
        Connection connection = DBHelper.getConnection();
        try {
            if (!checkExisting(connection, leaveRequest)) {// If there is no request existing under the username, add a new request
                insertNewRequest(leaveRequest, connection);
                respondSuccess(resp);
            } else {
                respondExisting(resp);
            }
        } catch (SQLException e) {
            LoggerHelper.warning("An exception was threw when trying to insert a new leave request to the database:");
            e.printStackTrace();
        }
    }

    private void insertNewRequest(LeaveRequest leaveRequest, Connection connection) throws SQLException {
        String sql = "INSERT INTO LeaveRequest " +
                "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, leaveRequest.getUsername());
        preparedStatement.setString(2, leaveRequest.getType());
        preparedStatement.setDate(3, leaveRequest.getStartDate());
        preparedStatement.setDate(4, leaveRequest.getEndDate());
        preparedStatement.setInt(5, leaveRequest.getDuration());
        preparedStatement.setString(6, leaveRequest.getReason());
        preparedStatement.setString(7, "待审批");

        preparedStatement.executeUpdate();
    }


    private void respondSuccess(HttpServletResponse resp) {
        try {
            PrintWriter writer = resp.getWriter();
            LeaveRespond leaveRespond = new LeaveRespond(true);
            writer.print(new Gson().toJson(leaveRespond));
        } catch (IOException e) {
            LoggerHelper.warning("An exception was threw when trying to respond leave-submit-success message:");
            e.printStackTrace();
        }

    }

    private void respondExisting(HttpServletResponse resp) {
        try {
            PrintWriter writer = resp.getWriter();
            writer.print("existingException=true");
        } catch (IOException e) {
            LoggerHelper.warning("An exception was threw when trying to respond 'Your leave request already exists':");
            e.printStackTrace();
        }
    }

    private boolean checkExisting(Connection connection, LeaveRequest leaveRequest) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM LeaveRequest WHERE 账号 = " + leaveRequest.getUsername());
        return resultSet.next();
    }
}



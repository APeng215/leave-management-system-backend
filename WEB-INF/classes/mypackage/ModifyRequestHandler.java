package mypackage;

import com.google.gson.Gson;
import mypackage.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class ModifyRequestHandler extends HttpServlet {
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
            } else {
                if(!checkApproved(connection, leaveRequest)){
                    updateRequest(leaveRequest, connection);
                    respondSuccess(resp);
                }

            }
        } catch (SQLException e) {
            LoggerHelper.warning("An exception was threw when trying to insert a new leave request to the database:");
            e.printStackTrace();
        }
    }

    private boolean checkApproved(Connection connection, LeaveRequest leaveRequest) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT 审批状态 FROM LeaveRequest WITH (TABLOCK) WHERE 账号 = " + leaveRequest.getUsername());
        if(resultSet.next()) {
            String state = resultSet.getString(1);
            return state.equals("已批准");
        }
        return false;
    }

    private void updateRequest(LeaveRequest leaveRequest, Connection connection) throws SQLException {
        String sql = "UPDATE LeaveRequest " +
                "SET 请假类型 = ?, 开始日期 = ?, 结束日期 = ?, 请假时长 = ?, 请假原因 = ?, 审批状态 = '待审批', 更新时间 = GETDATE() " +
                "WHERE 账号 = ?";  // Assuming you have a unique identifier like RequestID

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, leaveRequest.getType());
        preparedStatement.setDate(2, leaveRequest.getStartDate());
        preparedStatement.setDate(3, leaveRequest.getEndDate());
        preparedStatement.setInt(4, leaveRequest.getDuration());
        preparedStatement.setString(5, leaveRequest.getReason());
        preparedStatement.setString(6, leaveRequest.getUsername());

        preparedStatement.executeUpdate();
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
        ResultSet resultSet = statement.executeQuery("SELECT * FROM LeaveRequest WITH (TABLOCK) WHERE 账号 = " + leaveRequest.getUsername());
        return resultSet.next();
    }
}

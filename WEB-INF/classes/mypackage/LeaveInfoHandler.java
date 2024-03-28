package mypackage;

import com.google.gson.Gson;
import mypackage.util.DBHelper;
import mypackage.util.IDExpirationHelper;
import mypackage.util.LeaveInfo;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LeaveInfoHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
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
            Connection connection = DBHelper.getConnection();
            Statement statement = connection.createStatement();
            String duty = getDutyOfUser(req, statement);
            if (duty.equals("辅导员")) {
                ResultSet resultSet = selectDepartmentInfo(req, statement);
                ArrayList<LeaveInfo> leaveInfos = new ArrayList<>();
                loadLeaveInfos(resultSet, leaveInfos);
                respondLeaveInfos(resp, leaveInfos);
            } else {
                ResultSet resultSet = selectPersonalInfo(req, statement);
                ArrayList<LeaveInfo> leaveInfos = new ArrayList<>();
                loadLeaveInfos(resultSet, leaveInfos);
                respondLeaveInfos(resp, leaveInfos);
            }
        } catch (SQLException e) {
            System.out.println("Occur a SQLException when trying to select the leave request info:");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Occur a NullPointerException when trying to select the leave request info:");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Occur an IOExceptionException when trying to select the leave request info:");
            e.printStackTrace();
        }

    }

    private void respondLeaveInfos(HttpServletResponse resp, ArrayList<LeaveInfo> leaveInfos) throws IOException {
        String respMessage = new Gson().toJson(leaveInfos);
        PrintWriter writer = resp.getWriter();
        writer.print(respMessage);
    }

    private ResultSet selectPersonalInfo(HttpServletRequest req, Statement statement) throws SQLException {
        String sql = "SELECT 姓名, [学号/工号], 请假类型, 开始日期, 结束日期, 请假原因, 审批状态, 创建时间, 更新时间 " +
                "FROM LeaveRequest WITH (TABLOCK) INNER JOIN PersonalInfo ON LeaveRequest.账号 = PersonalInfo.[学号/工号] " +
                "WHERE [学号/工号] = " + req.getSession().getAttribute("username");
        return statement.executeQuery(sql);
    }

    private void loadLeaveInfos(ResultSet resultSet, ArrayList<LeaveInfo> leaveInfos) throws SQLException {
        while (resultSet.next()) {
            LeaveInfo temp = new LeaveInfo(
                    resultSet.getString("姓名"),
                    resultSet.getString("学号/工号"),
                    resultSet.getString("请假类型"),
                    resultSet.getDate("开始日期"),
                    resultSet.getDate("结束日期"),
                    resultSet.getString("请假原因"),
                    resultSet.getString("审批状态"),
                    resultSet.getTimestamp("创建时间"),
                    resultSet.getTimestamp("更新时间")
            );
            leaveInfos.add(temp);
        }
    }

    private ResultSet selectDepartmentInfo(HttpServletRequest req, Statement statement) throws SQLException {
        String sql = "SELECT 院系 FROM PersonalInfo WITH (TABLOCK) WHERE [学号/工号]=" + req.getSession().getAttribute("username");
        ResultSet resultSet = statement.executeQuery(sql);
        String department = null;
        while (resultSet.next()) {
            department = resultSet.getString(1);
        }
        sql = "SELECT 姓名, [学号/工号], 请假类型, 开始日期, 结束日期, 请假原因, 审批状态, 创建时间, 更新时间 " +
                "FROM LeaveRequest WITH (TABLOCK) INNER JOIN PersonalInfo ON LeaveRequest.账号 = PersonalInfo.[学号/工号] " +
                "WHERE 院系 = '" + department + "'";
        return statement.executeQuery(sql);
    }

    private String getDutyOfUser(HttpServletRequest req, Statement statement) throws SQLException {
        String sql = "SELECT 身份 FROM PersonalInfo WITH (TABLOCK) WHERE [学号/工号]=" + req.getSession().getAttribute("username");
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) return resultSet.getString(1);
        return null;
    }
}

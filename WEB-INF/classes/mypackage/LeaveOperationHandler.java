package mypackage;

import mypackage.util.DBHelper;
import mypackage.util.IDExpirationHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaveOperationHandler extends HttpServlet {
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
            return;
        }

        // Serve
        String operation = req.getParameter("operation");
        String targetUsername = req.getParameter("targetUsername");
        // Operation check
        try {
            switch (operation) {
                case "REVOKE":
                    if (!req.getSession().getAttribute("username").equals(targetUsername)) return;
                    String sql = "DELETE FROM LeaveRequest WHERE 账号 = " + targetUsername;
                    DBHelper.executeUpdate(sql);
                    break;
                case "APPROVE":
                    if (departmentCheck(req, targetUsername) && statusCheck(req, targetUsername) && dutyCheck(req))
                        approveRequest(targetUsername);
                    break;
                case "REJECT":
                    if (departmentCheck(req, targetUsername) && statusCheck(req, targetUsername) && dutyCheck(req))
                        rejectRequest(targetUsername);
                    break;

            }
        } catch (SQLException e) {
            System.out.println("Occur an exception when deleting a leave request:");
            e.printStackTrace();
        }


    }

    private void rejectRequest(String targetUsername) throws SQLException {
        DBHelper.executeUpdate("UPDATE LeaveRequest SET 审批状态 = '已拒绝' WHERE 账号 = " + targetUsername);
    }

    private void approveRequest(String targetUsername) throws SQLException {
        DBHelper.executeUpdate("UPDATE LeaveRequest SET 审批状态 = '已批准' WHERE 账号 = " + targetUsername);
    }

    private boolean dutyCheck(HttpServletRequest req) throws SQLException {
        ResultSet resultSet = DBHelper.executeQuery("SELECT 身份 FROM PersonalInfo WITH (TABLOCK) WHERE [学号/工号] = " + req.getSession().getAttribute("username"));
        if (resultSet.next()) {
            return resultSet.getString(1).equals("辅导员");
        }
        return false;
    }

    private boolean departmentCheck(HttpServletRequest req, String targetUsername) throws SQLException {
        return getDepartment((String) req.getSession().getAttribute("username")).equals(getDepartment(targetUsername));
    }

    private String getDepartment(String username) throws SQLException {
        ResultSet resultSet = DBHelper.executeQuery("SELECT 院系 FROM PersonalInfo WITH (TABLOCK) WHERE [学号/工号] = " + username);
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "NULL";
    }

    private boolean statusCheck(HttpServletRequest req, String targetUsername) throws SQLException {
        ResultSet resultSet = DBHelper.executeQuery("SELECT 审批状态 FROM LeaveRequest WITH (TABLOCK) WHERE 账号 = " + targetUsername);
        if (resultSet.next()) {
            return resultSet.getString(1).equals("待审批");
        }
        return false;
    }
}

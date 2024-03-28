package mypackage.util;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

public class LeaveRequest {
    String username;
    String type;
    Date startDate;
    Date endDate;
    int duration;
    String reason;

    public LeaveRequest(String username, String type, Date startDate, Date endDate, int duration, String reason) {
        this.username = username;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.reason = reason;
    }

    public static LeaveRequest ofRequest(HttpServletRequest req) {
        String username = (String) req.getSession().getAttribute("username");
        String type = URLDecoder.decode(req.getParameter("type"), StandardCharsets.UTF_8);
        Date startDate = Date.valueOf(req.getParameter("startDate"));
        Date endDate = Date.valueOf(req.getParameter("endDate"));
        int duration = Integer.parseInt(req.getParameter("duration"));
        String reason = URLDecoder.decode(req.getParameter("reason"), StandardCharsets.UTF_8);

        return new LeaveRequest(username, type, startDate, endDate, duration, reason);
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
    }
}

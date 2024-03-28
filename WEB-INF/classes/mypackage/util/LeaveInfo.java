package mypackage.util;

import java.sql.Timestamp;
import java.util.Date;

public class LeaveInfo {
    private String name;
    private String username;
    private String type;
    private Date beginDate, endDate;
    private String reason;
    private String approvalStatus;
    private Timestamp createTime, updateTime;

    public LeaveInfo() {
    }

    // Constructors
    public LeaveInfo(String name, String username, String type, Date beginDate, Date endDate,
                     String reason, String approvalStatus, Timestamp createTime, Timestamp updateTime) {
        this.name = name;
        this.username = username;
        this.type = type;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.reason = reason;
        this.approvalStatus = approvalStatus;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }
}

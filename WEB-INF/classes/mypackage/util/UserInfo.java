package mypackage.util;

public class UserInfo {
    private String name, username, id, sex, department, duty;

    public UserInfo() {
    }

    public UserInfo(String name, String username, String id, String sex, String department, String duty) {
        this.name = name;
        this.username = username;
        this.id = id;
        this.sex = sex;
        this.department = department;
        this.duty = duty;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getSex() {
        return sex;
    }

    public String getDepartment() {
        return department;
    }

    public String getDuty() {
        return duty;
    }
}

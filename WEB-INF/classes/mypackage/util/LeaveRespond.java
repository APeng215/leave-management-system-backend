package mypackage.util;

public class LeaveRespond {
    boolean submitSucceed = false;
    String message = null;

    public LeaveRespond() {
    }

    public LeaveRespond(boolean submitSucceed) {
        this.submitSucceed = submitSucceed;
    }

    public LeaveRespond(boolean submitSucceed, String message) {
        this.submitSucceed = submitSucceed;
        this.message = message;
    }
}

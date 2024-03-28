package mypackage.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class IDExpirationHelper {
    public static void respondIDExpiration(HttpServletResponse resp) {
        try {
            PrintWriter printWriter = resp.getWriter();
            printWriter.print("IDExpired=true");
        } catch (IOException e) {
            LoggerHelper.warning("Occur an exception when trying to respond ID expiration:");
            e.printStackTrace();
        }


    }
}

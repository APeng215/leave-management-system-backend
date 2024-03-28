package mypackage.util;

import java.util.logging.Logger;

public class LoggerHelper {
    private static final Logger logger = Logger.getLogger("logger");

    public static void warning(String message) {
        logger.warning(message);
    }
}

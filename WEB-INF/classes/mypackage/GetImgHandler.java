package mypackage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@WebServlet(urlPatterns = { "/getImg" })
public class GetImgHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Preparation
        resp.setHeader("Access-Control-Allow-Origin", getServletContext().getInitParameter("frontIp"));
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        FileInputStream fileInputStream = new FileInputStream("./HeadSculpture/" + req.getSession().getAttribute("username") + ".png");
        resp.getOutputStream().write(fileInputStream.readAllBytes());
    }
}

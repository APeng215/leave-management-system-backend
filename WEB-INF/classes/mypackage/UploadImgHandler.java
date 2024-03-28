package mypackage;

import mypackage.util.ImgHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(urlPatterns = { "/uploadImg" })
@MultipartConfig()
public class UploadImgHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Preparation
        resp.setHeader("Access-Control-Allow-Origin", getServletContext().getInitParameter("frontIp"));
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        System.out.println("Upload");
    try {
        new File("./HeadSculpture").mkdirs();
        Part filePart = req.getPart("file");
        String disposition = filePart.getHeader("Content-Disposition");
        InputStream inputStream = filePart.getInputStream();
        String suffix = disposition.substring(disposition.lastIndexOf('.') + 1, disposition.length() -1 );

        if(!(suffix.equals("jpg") || suffix.equals("png") || suffix.equals("PNG") || suffix.equals("JPG"))){
            inputStream.close();
            return;
        }
        String filePathString = "./HeadSculpture/" + req.getSession().getAttribute("username") + ".png";
        FileOutputStream fileOutputStream = new FileOutputStream(filePathString);
        fileOutputStream.write(inputStream.readAllBytes());


        ImgHelper.resize(Paths.get(filePathString), Paths.get(filePathString), 70, 70);
        fileOutputStream.close();
        inputStream.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    }
}

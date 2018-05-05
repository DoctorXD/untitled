package com.Servlet;

import com.Bean.PhotoBase;
import com.DBTool.DBUtil;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

;

public class Upload extends HttpServlet {
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e){}
    }

    /**
     * 通过BASE64Decoder解码，并生成图片
     * @param imgStr 解码后的string
     */
    public boolean string2Image(String imgStr, String imgFilePath) {
        // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null)
            return false;
        try {
            // Base64解码
            byte[] b = new BASE64Decoder().decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    // 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成Jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Statement sql = null;
        PhotoBase reg = new PhotoBase();

        String path = request.getParameter("base64Array");
        String name= request.getParameter("name");

        boolean flag = string2Image(path,name);

        boolean type=false;
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String url = "jdbc:mysql://localhost:3306/web";
        String username = "root";
        String SQLpassword = "123123";
        try
        {
            Connection con = DriverManager.getConnection( url ,username, SQLpassword);

            Statement stmt=con.createStatement();
            //System.out.println(ID+"yxdstc5");
            String insertRecord = "('"+path+"','"+name+"')";
            String insertCondition = "INSERT INTO UploadPhoto VALUES"+insertRecord;
            int m =sql.executeUpdate(insertCondition);
            if(m!=0) {
                reg.setBase64Array(path);
                reg.setName(name);
            }

            ResultSet rs=stmt.executeQuery(String.valueOf(sql));
            while(rs.next())
            {
                type=true;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            DBUtil.Close();
            out.print(type);
            out.flush();
            out.close();
        }
    }
    public  void  doGet(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException
    {
        doPost(request,response);
    }

}
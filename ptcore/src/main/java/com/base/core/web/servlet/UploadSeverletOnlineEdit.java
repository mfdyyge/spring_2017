package com.base.core.web.servlet;

import com.base.core.web.JsonHelp;
import com.oreilly.servlet.MultipartRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * <ol>
 * date:12-6-10 editor:yanghongjian
 * <li>创建文档</li>
 * <li>在线编辑器需要的上传组件</li>
 * <li>使用方式
 * <pre>
 *     在web.xml添加
 *    <servlet>
 * <servlet-name>uploadSeverletOnlineEdit</servlet-name>
 * <display-name>上传文件</display-name>
 * <description>上传文件</description>
 * <servlet-class>com.ypt.web.servlet.UploadSeverletOnlineEdit</servlet-class>
 * <load-on-startup>0</load-on-startup>
 * </servlet>
 * <servlet-mapping>
 * <servlet-name>uploadSeverletOnlineEdit</servlet-name>
 * <url-pattern>/fileUpLoad</url-pattern>
 * </servlet-mapping>
 * </pre>
 * </li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class UploadSeverletOnlineEdit extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int code = 0;
        String msg = "上传成功!";

        String uploadPh = System.getProperty("os.name").toLowerCase().indexOf("windows") != -1
                ? JsonHelp.getPropertiesByKey("UPLOAD_PATH_WIN")
                : JsonHelp.getPropertiesByKey("UPLOAD_PATH_LINUX");
        System.out.println("1 uploadPh="+uploadPh);
        //存相对路径
        String filePath = request.getRealPath("/") +File.separator+ "upload";
        System.out.println("2 getRealPath="+filePath);
        if (!uploadPh.isEmpty())
            filePath = uploadPh;

        File uploadPath = new File(filePath);
        System.out.println("3 filePath="+uploadPath.getPath());
        //检查文件夹是否存在 不存在 创建一个
        if (!uploadPath.exists()) {
            if (!uploadPath.mkdir()) {
                code = -1;
                msg = "不能够创建上传文件夹[" + filePath + "]";
                creatJsonStr(request, response, msg, "");
            }
        }
        System.out.println("4 server path="+uploadPath.getPath());
        //文件最大容量 5M
        int fileMaxSize = 5 * 1024 * 1024;
        //文件名
        String fileName;
        //上传文件数
        int fileCount = 0;
        //重命名策略    如果需要重新按照时间或则其他命名则需要该方法
        // RandomFileRenamePolicy rfrp=new RandomFileRenamePolicy();
        // MultipartRequest mulit = new MultipartRequest(request, filePath, fileMaxSize, "utf-8",rfrp);

        //上传文件
        MultipartRequest mulit;
        String newPath = "";
        try {
//            mulit = new MultipartRequest(request, filePath, fileMaxSize, "utf-8");
            mulit = new MultipartRequest(request, filePath, fileMaxSize);
            Enumeration filesname = mulit.getFileNames();
            while (filesname.hasMoreElements()) {
//                String name = (String) filesname.nextElement();
//                fileName = mulit.getFilesystemName(name);
//                String contentType = mulit.getContentType(name);
//                if (fileName != null) {
//                    fileCount++;
//                }

                String name = (String) filesname.nextElement();// 文件文本框的名称
                // 获取该文件框中上传的文件，即对应到上传到服务器中的文件
                File uploadFile = mulit.getFile(name);
                System.out.println("5 uploadFile文件名：" + uploadFile.getPath());
                System.out.println("6 uploadFile文件类型： " + mulit.getContentType(name));

                if (null != uploadFile && uploadFile.length() > 0) {
                    String path = uploadFile.getName();
                    fileName = mulit.getFilesystemName(name);
                    if (fileName != null) {
                        fileCount++;
                    }
                    //imgPath为原文件名
                    int idx = path.lastIndexOf(".");
                    //文件后缀
                    String extention = path.substring(idx);

                    String time = (new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date(System
                            .currentTimeMillis()));

                    //新的文件名(日期+后缀)
                    newPath = time + extention;
                    File f = new File(uploadPath + File.separator + newPath);
                    uploadFile.renameTo(f);
                    System.out.println("7 save upload 文件名：" + f.getPath());
                }
            }
        } catch (Exception e) {
            code = -1;
            msg = e.getMessage();
            e.printStackTrace();
        }
        System.out.println("共上传" + fileCount + "个文件！");
        if (code == -1)
            creatJsonStr(request, response, msg, "");
        else
            creatJsonStr(request, response, "", "/upload/" + newPath);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    private String creatJsonStr(HttpServletRequest request, HttpServletResponse response, String err, String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"err\":\"").append(err.replaceAll("\"", "\\\"")).append("\",");
        sb.append("\"msg\":\"").append(msg).append("\"");
        sb.append("}");
        ajaxResponse(request, response, sb.toString());
        return null;
    }

    /**
     * ajax请求返回
     *
     * @param strRet 返回消息
     */
    private void ajaxResponse(HttpServletRequest request, HttpServletResponse response, String strRet) {
        response.setHeader("Charset", "UTF-8");
        //返回的是html文本文件
        //必须加否则会出现下载框
        response.setContentType("text/html;charset=UTF-8");

//        String strJson = JsonHelp.convert(strRet);
        String strJson = strRet;
        String jsoncallback = request.getParameter("callback") == null ? "" : request.getParameter("callback");
        strJson = jsoncallback.length() == 0 ? strJson : jsoncallback + "(" + strJson + ")";
        System.out.println(strJson);
        try {
            PrintWriter out = response.getWriter();
            out.write(strJson);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

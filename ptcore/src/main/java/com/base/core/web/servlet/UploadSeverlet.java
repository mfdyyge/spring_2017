package com.base.core.web.servlet;

import com.base.core.web.JsonHelp;
import com.oreilly.servlet.MultipartRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <ol>
 * date:12-6-10 editor:yanghongjian
 * <li>创建文档</li>
 * <li>使用方式
 * <pre>
 *     在web.xml添加
 *    <servlet>
 * <servlet-name>uploadSeverlet</servlet-name>
 * <display-name>上传文件</display-name>
 * <description>上传文件</description>
 * <servlet-class>com.ypt.web.servlet.UploadSeverlet</servlet-class>
 * <load-on-startup>0</load-on-startup>
 * </servlet>
 * <servlet-mapping>
 * <servlet-name>uploadSeverlet</servlet-name>
 * <url-pattern>/fileUpLoad</url-pattern>
 * </servlet-mapping>
 * </pre>
 * </li>
 * <li>
 * <pre>
 *       request.getSession().getServletContect().getRealPath()得到站点的绝对地址
 * 在Servlet 和Struts中还可以用
 * this.getServletContect().getRealPath("/");
 * this.getServlet().getServletContect().getRealPath("/");
 * ServletActionContext.getServletContext().getRealPath("comm");
 * ActionContext ac = ActionContext.getContext();
 * ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
 * String path = sc.getRealPath("/");
 * request.getContextPath().toString(); 相对路径
 *     </pre>
 * </li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class UploadSeverlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int code = 0;
        String msg = "上传成功!";

        String uploadPh = System.getProperty("os.name").toLowerCase().indexOf("windows") != -1
                ? JsonHelp.getPropertiesByKey("UPLOAD_PATH_WIN")
                : JsonHelp.getPropertiesByKey("UPLOAD_PATH_LINUX");
        //得到站点的绝对路径
        String filePath = this.getServletContext().getRealPath("/") + File.separator + "upload";
        //存相对路径
//        filePath = request.getRealPath("/") + File.separator + "upload";
        if (!uploadPh.isEmpty())
            filePath = uploadPh;

        File uploadPath = new File(filePath);
        //检查文件夹是否存在 不存在 创建一个
        if (!uploadPath.exists()) {
            if (!uploadPath.mkdir()) {
                code = -1;
                msg = "不能够创建上传文件夹[" + uploadPath.getAbsolutePath() + "]";
                creatJsonStr(request, response, code, msg);
                return;
            }
        }
        //文件最大容量
//        int fileMaxSize = 5 * 1024;
        String fileMaxSize = System.getProperty("os.name").toLowerCase().indexOf("windows") != -1
        ? JsonHelp.getPropertiesByKey("UPLOAD_SIZE_WIN")
        : JsonHelp.getPropertiesByKey("UPLOAD_SIZE_LINUX");
        //文件名
        String fileName = null;
        //上传文件数
        int fileCount = 0;
        //重命名策略    如果需要重新按照时间或则其他命名则需要该方法
        // RandomFileRenamePolicy rfrp=new RandomFileRenamePolicy();
        // MultipartRequest mulit = new MultipartRequest(request, filePath, fileMaxSize, "utf-8",rfrp);

        //上传文件
        MultipartRequest mulit;
        String newPath = "";
        List listUpload = new ArrayList();
        try {
//            mulit = new MultipartRequest(request, filePath, fileMaxSize, "utf-8");
//            mulit = new MultipartRequest(request, filePath, fileMaxSize);
            mulit = new MultipartRequest(request, filePath,Integer.parseInt(fileMaxSize));//2015-12-21 dingshuangbo 修改文件大小限制
            Enumeration filesname = mulit.getFileNames();
            while (filesname.hasMoreElements()) {
//                String name = (String) filesname.nextElement();
//                fileName = mulit.getFilesystemName(name);
//                String contentType = mulit.getContentType(name);
//                if (fileName != null) {
//                    fileCount++;
//                }
//                System.out.println("文件名：" + fileName);
//                System.out.println("文件类型： " + contentType);
                String name = (String) filesname.nextElement();// 文件文本框的名称
                // 获取该文件框中上传的文件，即对应到上传到服务器中的文件
                File uploadFile = mulit.getFile(name);

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

//                    java.util.Date dt = new java.util.Date(System
//                            .currentTimeMillis());
//                    SimpleDateFormat fmt = new SimpleDateFormat(
//                            "yyyyMMddHHmmssSSS");
//                    String time = fmt.format(new java.util.Date(System
//                            .currentTimeMillis()));

                    //新的文件名(日期+后缀)
//                    newPath = fmt.format(new java.util.Date(System
//                            .currentTimeMillis())) + extention;
                    //新的文件名(日期+后缀)
                    File f = new File(uploadPath + File.separator + (new SimpleDateFormat(
                            "yyyyMMddHHmmssSSS").format(new java.util.Date(System
                            .currentTimeMillis())) + extention));
                    if(uploadPh.isEmpty())
                        listUpload.add("/upload/"+File.separator+f.getName());
                    else
                        listUpload.add(f.getAbsoluteFile());

                    System.out.println("源文件 " + uploadFile.getName());
                    System.out.println("上传存储 " + f.getAbsolutePath());
                    uploadFile.renameTo(f);
                }
            }
        } catch (Exception e) {
            code = -1;
            msg = e.getMessage();
            e.printStackTrace();
            creatJsonStr(request, response, code, msg);
            return;
        }

        System.out.println("共上传 " + fileCount + "个文件！");
        creatJsonStr(request, response, code, StringUtils.join(listUpload,","));
//        creatJsonStr(request, response, code, "/upload/" + newPath);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    private String creatJsonStr(HttpServletRequest request, HttpServletResponse response, int code, String msg) {
        ajaxResponse(request, response, JsonHelp.format("{'code':'$code$','msg':'$msg$'}", (ArrayUtils.toMap(new String[][]{
                {"code", String.valueOf(code)},
                {"msg", String.valueOf(msg)}
        }))));
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

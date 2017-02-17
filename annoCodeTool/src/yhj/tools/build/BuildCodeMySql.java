package yhj.tools.build;

import freemarker.template.Configuration;
import freemarker.template.Template;
import yhj.tools.ibatis.DbTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <ol>
 * date:11-10-14 editor:yanghongjian
 * <li>创建文档</li>
 * <li>使用freeMark模板生成基于MySql的基础代码</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class BuildCodeMySql {
    public BuildCodeMySql() {
        dbTools = DbTools.getInstance();
    }
    public BuildCodeMySql(String ProperName) {
        dbTools = DbTools.getInstance(ProperName);
    }

    private static DbTools dbTools ;
    private Map mapParam = new HashMap();
    private ArrayList al;
    private String tabelName;
    private String tabelComments;
    private Map mapColcomments;
    private String namespace = "";
    private Map mapPriKey = new HashMap();
    private Map mapIndKey = new HashMap();

    private Map root = new HashMap();

    /**
     * 字符串编码转换为GBK
     *
     * @param str 被处理的字符串
     * @return String 将数据库中读出的数据进行转换
     */
    public static String convert(String str) {
        if (str == null || str.length() == 0)
            str = "";
        str = str.trim();
//        if (str.length() > 0) {
//            try {
//                str = new String(str.getBytes("ISO8859_1"), "GBK");
//            } catch (Exception e) {
//                str = str;
//            }
//        }
        return str;
    }

    /**
     * 获取表主键
     *
     * @param tableName 表名
     */
    private void getTablePriKey(String tableName) {
        mapPriKey.clear();
        String sql = "SHOW KEYS FROM " + tableName;
        ResultSet rs = dbTools.getResultSet(sql);
        try {
            while (rs.next()) {
                mapPriKey.put(rs.getString(5).toUpperCase(), getFieldComments(rs.getString(5).toUpperCase()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        mapIndKey.clear();
        sql = "SHOW INDEX FROM " + tableName;
        rs = dbTools.getResultSet(sql);
        try {
            while (rs.next()) {
                mapIndKey.put(rs.getString(5).toUpperCase(), getFieldComments(rs.getString(5).toUpperCase()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取表、列的注释
     *
     * @param tableName 表名
     */
    private void getTableColComments(String tableName) {
        System.out.println("开始生成"+tableName+"相关代码、配置...");
        String sql = "SHOW TABLE STATUS FROM `" + dbTools.getDBMS_DB()+"`";
        ResultSet rs = dbTools.getResultSet(sql);
        try {
            while (rs.next()) {
                if (rs.getString(1).toUpperCase().equals(tableName)) {
                    tabelComments = rs.getString(18) != null ? rs.getString(18) : "";
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        sql = "SHOW FULL COLUMNS FROM " + tableName;
        rs = dbTools.getResultSet(sql);
        mapColcomments = new HashMap();
        try {
            while (rs.next())
                mapColcomments.put(rs.getString(1), convert(rs.getString(9)));
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        namespace = DbTools.getInstance().getSQLMAP_NAMESPACE();
        namespace = namespace.length() > 0 ? namespace : tableName;
    }

    /**
     * 获取列的注释
     *
     * @param colName 列名
     * @return 返回该列的注释
     */
    private String getFieldComments(String colName) {
        return (String) mapColcomments.get(colName);
    }

    public void fillDllArrayList(String tableName, String sqlName, String javaName) {
        this.tabelName = tableName;
        //try{
        if (al == null)
            al = new ArrayList();
        else
            al.clear();
        try {
            getTableColComments(tableName);
            getTablePriKey(tableName);//取表主键

            String sql = "select * from " + tableName + " t limit 1,1";

            ResultSet rs = dbTools.getResultSet(sql);

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                Map mapDdl = new HashMap();
                mapDdl.put("fieldName", resultSetMetaData.getColumnName(i));
                mapDdl.put("javaAtrriber", mapDdl.get("fieldName"));
                mapDdl.put("fieldSqlType", resultSetMetaData.getColumnTypeName(i));

                mapDdl.put("javaType", "String");
                mapDdl.put("sqlMapType", "");
                mapDdl.put("sqlMapJavaType", "string");

                if (mapDdl.get("fieldSqlType").equals("CHAR")
                        || mapDdl.get("fieldSqlType").equals("VARCHAR")
                        || mapDdl.get("fieldSqlType").equals("TEXT")
                        || mapDdl.get("fieldSqlType").equals("BLOB")) {//VARCHAR
                    mapDdl.put("javaType", "String");
                    mapDdl.put("sqlMapType", "VARCHAR");
                    mapDdl.put("sqlMapJavaType", "string");
                }
                if (mapDdl.get("fieldSqlType").equals("DATE")
                        ||mapDdl.get("fieldSqlType").equals("DATETIME")) {//DATE
                    mapDdl.put("fieldSqlType", "DATE");
                    mapDdl.put("sqlMapType", "DATETIME");
                }
                if (mapDdl.get("fieldSqlType").equals("SMALLINT")
                        ||mapDdl.get("fieldSqlType").equals("BIGINT")
                        ||mapDdl.get("fieldSqlType").equals("INT")
                        ||mapDdl.get("fieldSqlType").equals("FLOAT")
                        ||mapDdl.get("fieldSqlType").equals("DOUBLE")
                        ||mapDdl.get("fieldSqlType").equals("DECIMAL")
                         ||mapDdl.get("fieldSqlType").equals("TINYINT")
                        ) {
                    mapDdl.put("javaType", "String");
//                    mapDdl.put("sqlMapType", "DECIMAL");
                    mapDdl.put("sqlMapType", "NUMERIC");
                    mapDdl.put("sqlMapJavaType", "string");
                }
                mapDdl.put("fieldComments", getFieldComments((String) mapDdl.get("fieldName")));

                System.out.println("-->" + mapDdl);
                al.add(mapDdl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //创建模板数据模型
        root = new HashMap();
        root.put("date", DbTools.getCurDate());

        root.put("sqlName", sqlName);
        root.put("javaName", javaName);
        root.put("tabelComments", tabelComments);
        root.put("tableCols", al);
        root.put("colAlias", DbTools.getPropertiesVal("table_alias"));
        root.put("mapPriKey", mapPriKey);
        root.put("mapIndKey", mapIndKey);

        root.put("daoPath", DbTools.getPropertiesVal("daoPath"));
        root.put("boPath", DbTools.getPropertiesVal("boPath"));
        root.put("actionPath", DbTools.getPropertiesVal("actionPath"));

        root.put("mapProp", dbTools.getMapProperties());
        cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");//解决模板中文输出乱码问题
//        cfg.setDirectoryForTemplateLoading(new File("bin/freemarker"));
        cfg.setClassForTemplateLoading(this.getClass(), "/yhj/template");
    }

    private Configuration cfg;

    public Configuration getCfg() {
        return cfg;
    }

    /**
     * 动态使用freeMark模板技术生成需要的文件
     *
     * @param mapParam <ol>参数值:
     *                 <li>FTL_NAME 模板名称</li>
     *                 <li>JAVA_NAME 生成的java名称</li>
     *                 <li>SQL_NAME 生成的SQL文件名称</li>
     *                 <li>FILE_EXT_NAME 扩展文件名</li>
     *                 </ol>
     * @return true执行成功否则执行出错
     */
    private boolean createByTemplate(Map mapParam) {
        try {
            Template sqlMapTemplate = cfg.getTemplate(dbTools.getMapByKey(mapParam, "FTL_NAME"));
            System.out.println("使用模板文件->"+dbTools.getMapByKey(mapParam, "FTL_NAME"));
//            Writer out = new OutputStreamWriter(new FileOutputStream(sqlName+".xml"), "UTF-8");

            String strFilePath = DbTools.getPropertiesVal("code_dir") + File.separator + dbTools.getMapByKey(mapParam, "FILE_PATH");
            //2015.7.2 yanghongjian 解决指定完整生成路径的问题
            String path = dbTools.getMapByKey(mapParam, "FILE_PATH");
            strFilePath = path.indexOf(":") != -1?path:strFilePath;

            File filePath = new File(strFilePath);
            if (!filePath.exists()) {
                if (filePath.mkdirs())
                    System.out.println("生成目录:" + strFilePath);
                else {
                    System.out.println("不能够生成目录:" + strFilePath);
                    return false;
                }
            }
            String fileName = strFilePath + File.separator
                    + dbTools.getMapByKey(mapParam, "FILE_NAME")
                    + dbTools.getMapByKey(mapParam, "FILE_EXT_NAME");
            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileName), DbTools.getPropertiesVal("code_encode"));

            sqlMapTemplate.process(root, out);
            out.flush();
            out.close();
            System.out.println("-->生成文件:" + fileName+"\n");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void createSqlMap(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("sqlmapPath");
        mapParam.put("FTL_NAME","sql_map_"+DbTools.getPropertiesVal("dbms_type")+".ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", fileName);
        mapParam.put("FILE_EXT_NAME", ".xml");
        createByTemplate(mapParam);
    }

    public void createDao(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("daoPath").replace('.','\\');
        mapParam.put("FTL_NAME", "dao_interface.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", "IDao" + fileName);
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);

        mapParam.clear();
        mapParam.put("FTL_NAME", "dao_impl.ftl");
        mapParam.put("FILE_PATH", filePath + File.separator + "impl");
        mapParam.put("FILE_NAME", "Dao" + fileName + "Impl");
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);
    }

    public void createBo(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("boPath").replace('.','\\');
        mapParam.put("FTL_NAME", "bo_interface.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", "IBo" + fileName);
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);

        mapParam.clear();
        mapParam.put("FTL_NAME", "bo_impl.ftl");
        mapParam.put("FILE_PATH", filePath + File.separator + "impl");
        mapParam.put("FILE_NAME", "Bo" + fileName + "Impl");
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);

//        mapParam.clear();
//        filePath = DbTools.getPropertiesVal("boTestPath").replace('.','\\');
//        mapParam.put("FTL_NAME", "bo_impl_test.ftl");
//        mapParam.put("FILE_PATH", filePath + File.separator + "ImplTest");
//        mapParam.put("FILE_NAME", "Bo" + fileName + "ImplTest");
//        mapParam.put("FILE_EXT_NAME", ".java");
//        createByTemplate(mapParam);
    }
    public void createService(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("servicePath").replace('.','\\');
        mapParam.put("FTL_NAME", "service_interface.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", "IService" + fileName);
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);

        mapParam.clear();
        mapParam.put("FTL_NAME", "service_impl.ftl");
        mapParam.put("FILE_PATH", filePath + File.separator + "impl");
        mapParam.put("FILE_NAME", "Service" + fileName + "Impl");
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);
 
    }

    public void createConfig(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("confPath").replace('.','\\');
        String tem = filePath.split("\\"+File.separator)[0];
        mapParam.put("FTL_NAME", "spring-dubbo-conf.ftl");
        mapParam.put("FILE_PATH", tem);
        mapParam.put("FILE_NAME", fileName + "-CONF");
        mapParam.put("FILE_EXT_NAME", ".xml");
        createByTemplate(mapParam);
    }

    public void createAction(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("actionPath").replace('.','\\');
        mapParam.put("FTL_NAME", "action.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", fileName + "Action");
        mapParam.put("FILE_EXT_NAME", ".java");
        createByTemplate(mapParam);
    }

    public void createPage(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("pagePath").replace('.','\\');
        String tem = filePath.split("\\"+File.separator)[0];
        mapParam.put("FTL_NAME","page_index.ftl");
        mapParam.put("FILE_PATH", tem);
        mapParam.put("FILE_NAME", fileName+"_index");
        mapParam.put("FILE_EXT_NAME", ".html");
        createByTemplate(mapParam);

        mapParam.clear();
        mapParam.put("FTL_NAME", "page_index.js.ftl");
        mapParam.put("FILE_PATH", tem);
        mapParam.put("FILE_NAME", fileName+"_index");
        mapParam.put("FILE_EXT_NAME", ".js");
        createByTemplate(mapParam);

        mapParam.clear();
        mapParam.put("FTL_NAME","page_save.ftl");
        mapParam.put("FILE_PATH", tem);
        mapParam.put("FILE_NAME", fileName+"_save");
        mapParam.put("FILE_EXT_NAME", ".html");
        createByTemplate(mapParam);

        mapParam.clear();
        mapParam.put("FTL_NAME","page_save.js.ftl");
        mapParam.put("FILE_PATH", tem);
        mapParam.put("FILE_NAME", fileName+"_save");
        mapParam.put("FILE_EXT_NAME", ".js");
        createByTemplate(mapParam);
    }
    public void createWebList(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("webListPath").replace('.','\\');
        mapParam.put("FTL_NAME", "web_list.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", fileName + "_LIST");
        mapParam.put("FILE_EXT_NAME", ".jsp");
        createByTemplate(mapParam);
    }
    
    public void createWebShow(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("webListPath").replace('.','\\');
        mapParam.put("FTL_NAME", "web_show.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", fileName + "_SHOW");
        mapParam.put("FILE_EXT_NAME", ".jsp");
        createByTemplate(mapParam);
    }
    
    public void createWebAdd(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("webListPath").replace('.','\\');
        mapParam.put("FTL_NAME", "web_add.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", fileName + "_ADD");
        mapParam.put("FILE_EXT_NAME", ".jsp");
        createByTemplate(mapParam);
    }
    
    public void createWebUpdate(String fileName) {
        mapParam.clear();
        String filePath = DbTools.getPropertiesVal("webListPath").replace('.','\\');
        mapParam.put("FTL_NAME", "web_update.ftl");
        mapParam.put("FILE_PATH", filePath);
        mapParam.put("FILE_NAME", fileName + "_UPDATE");
        mapParam.put("FILE_EXT_NAME", ".jsp");
        createByTemplate(mapParam);
    }

    public static void main(String args[]) {
        BuildCodeMySql buildCode = new BuildCodeMySql();
//        BuildCode buildCode = new BuildCode("/db-zixun.properties");
//        BuildCode buildCode = new BuildCode("/db-dzg.properties");
//        BuildCode buildCode = new BuildCode("/db-lcs.properties");
//        BuildCode buildCode = new BuildCode("/db-shangpin.properties");
        String buildTabMc = DbTools.getPropertiesVal("BUILD_TAB_MC").trim();
        if (args.length == 0 && buildTabMc.length() == 0 ) {
            System.out.println("+---------------------------+");
            System.out.println("+\t参数不允许为空");
            System.out.println("+---------------------------+");
        }else {
            args =buildTabMc.indexOf(",")!=-1? buildTabMc.split(","):buildTabMc.split(" ");
            for (int i = 0; i < args.length; i++) {
                buildCode.fillDllArrayList(args[i].toUpperCase(), args[i].toUpperCase(), args[i].toUpperCase());
                buildCode.createSqlMap(args[i].toUpperCase());
                buildCode.createDao(args[i].toUpperCase());
                buildCode.createBo(args[i].toUpperCase());
                buildCode.createService(args[i].toUpperCase());
                buildCode.createAction(args[i].toUpperCase());
                buildCode.createWebList(args[i].toUpperCase());
                buildCode.createWebShow(args[i].toUpperCase());
                buildCode.createWebAdd(args[i].toUpperCase());
                buildCode.createWebUpdate(args[i].toUpperCase());
                
               // buildCode.createConfig(args[i].toUpperCase());

              //  buildCode.createPage(args[i].toUpperCase());
            }
        }
        dbTools.closeDb();
    }
}

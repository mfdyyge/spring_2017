package yhj.tools.ibatis;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2005-5-31
 * Time: 16:28:35
 * 数据库操作(CRUD)
 */
public final class DbTools {
    private static DbTools dbTools = null;
    private Connection conn;
    Statement stmt;
    private Map mapProperties = new HashMap();
    private String CODE_DIR = "";
    private String CODE_ENCODE = "GBK";
    private String DBMS_DB = "";
    private String SQLMAP_NAMESPACE = "";
    private String DRV = "";
    private String URL = "";
    private String USER = "";
    private String PASS = "";
    private String EDITOR = "";
    private String AUTHOR = "";
    private String TABLE_ALIAS = "";
    private String daoPath = "";
    private String boPath = "";
    private String actionPath = "";
    private String sqlMapClientName = "";
    private String txTransactionProxyName = "";

    /**
     * 单一实例
     *
     * @return 连接对象
     */
    public static synchronized DbTools getInstance() {
        if (dbTools == null) {
            dbTools = new DbTools();
            dbTools.init();
        }
        return dbTools;
    }

    public static synchronized DbTools getInstance(String ProperName) {
        if (dbTools == null) {
            dbTools = new DbTools();
            dbTools.init(ProperName);
        }
        return dbTools;
    }

    private DbTools() {

    }

    /**
     * 递归加载配置文件
     *
     * @param properFileName 配置文件名称
     * @param mapProper      存储节点，注意后加载的节点覆盖相同节点的值
     * @return 返回本配置文件的所有节点值
     */
    public static Map fillProper(String properFileName, Map mapProper) {
        Properties otherProperties = new Properties();
        try {
            otherProperties.load(DbTools.class.getResourceAsStream(properFileName));
            Enumeration en = otherProperties.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                mapProper.put(key, otherProperties.getProperty(key));
            }
            String strFiles = (String) mapProper.get("PROPER_FILES");
            if (strFiles == null || strFiles.trim().length() == 0)
                return mapProper;
            mapProper.remove("PROPER_FILES");

            String[] properFiles = strFiles.split(",");
            for (int i = 0; i < properFiles.length; i++) {
                System.out.println("加载辅助代码生成工具的扩展配置文件:" + properFiles[i]);
                mapProper = fillProper(properFiles[i], mapProper);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapProper;
    }

    public void init(String properName) {
        mapProperties = fillProper(properName, mapProperties);

        String DBMS_TYPE = mapProperties.get("dbms_type").toString();
        CODE_DIR = mapProperties.get("code_dir") + "";
        DBMS_DB = mapProperties.get("dbms_db").toString();
        CODE_ENCODE = mapProperties.get("code_encode").toString();
        SQLMAP_NAMESPACE = mapProperties.get("sqlmap_namespace").toString();
        DRV = mapProperties.get(DBMS_TYPE + "_driver").toString();
        URL = mapProperties.get(DBMS_TYPE + "_url").toString();

        USER = mapProperties.get(DBMS_TYPE + "_user").toString();
        PASS = mapProperties.get(DBMS_TYPE + "_pass").toString();
        EDITOR = mapProperties.get("editor").toString();
        AUTHOR = mapProperties.get("author").toString();
        TABLE_ALIAS = mapProperties.get("table_alias").toString();
        TABLE_ALIAS = TABLE_ALIAS.length() == 0 ? "T" : TABLE_ALIAS;

        daoPath = mapProperties.get("daoPath").toString();
        boPath = mapProperties.get("boPath").toString();
        actionPath = mapProperties.get("actionPath").toString();
        sqlMapClientName = mapProperties.get("sqlMapClientName").toString();
        txTransactionProxyName = mapProperties.get("txTransactionProxyName").toString();
        ConnByJdbc();
    }

    public void initOld(String ProperName) {
        mapProperties = fillProper(ProperName, mapProperties);

        Properties config = new Properties();
        try {
            config.load(this.getClass().getResourceAsStream(ProperName));
            mapProperties.clear();
            /*返回属性列表中所有键的枚举*/
            Enumeration enums = config.propertyNames();
            String key;
            while (enums.hasMoreElements()) {
                key = (String) enums.nextElement();
                mapProperties.put(key, config.getProperty(key));
                /*输出properties属性文件的内容*/
                System.out.println(key + "---->" + config.getProperty(key));
            }
            String DBMS_TYPE = config.getProperty("dbms_type");
            CODE_DIR = config.getProperty("code_dir") + "";
            DBMS_DB = config.getProperty("dbms_db");
            CODE_ENCODE = config.getProperty("code_encode");
            SQLMAP_NAMESPACE = config.getProperty("sqlmap_namespace");
            DRV = config.getProperty(DBMS_TYPE + "_driver");
            URL = config.getProperty(DBMS_TYPE + "_url");

            USER = config.getProperty(DBMS_TYPE + "_user");
            PASS = config.getProperty(DBMS_TYPE + "_pass");
            EDITOR = config.getProperty("editor");
            AUTHOR = config.getProperty("author");
            TABLE_ALIAS = config.getProperty("table_alias");
            TABLE_ALIAS = TABLE_ALIAS.length() == 0 ? "T" : TABLE_ALIAS;

            daoPath = config.getProperty("daoPath");
            boPath = config.getProperty("boPath");
            actionPath = config.getProperty("actionPath");
            sqlMapClientName = config.getProperty("sqlMapClientName");
            txTransactionProxyName = config.getProperty("txTransactionProxyName");
            ConnByJdbc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        init("/db.properties");
    }

    /**
     * 使用JDBC的URL方式连接数据库
     */
    private void ConnByJdbc() {
        try {
            Class.forName(DRV);
            //  DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            conn = DriverManager.getConnection(URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            closeDb();
        }
    }

    /**
     * 关闭数据库连接
     */
    public void closeDb() {

        try {
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    /**
     * 返回记录集
     * 注意使用完数据集后，调用 closeDb()释放数据连接
     *
     * @param querySql 查询语句
     * @return 返回记录集
     */
    public ResultSet getResultSet(String querySql) {
        ResultSet rs = null;
        try {
            stmt.close();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(querySql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getDBMS_DB() {
        return DBMS_DB;
    }

    public void setDBMS_DB(String DBMS_DB) {
        this.DBMS_DB = DBMS_DB;
    }

    public String getCODE_DIR() {
        File filePath = new File(CODE_DIR);
        if (!filePath.exists()) {
            filePath.mkdir();
            System.out.println("生成代码目录:" + CODE_DIR);
        }
        return CODE_DIR;
    }

    public void setCODE_DIR(String CODE_DIR) {
        File f = new File(CODE_DIR);
        if (!f.exists()) {
            f.mkdirs();
        }
        this.CODE_DIR = CODE_DIR;
    }

    public String getCODE_ENCODE() {
        return CODE_ENCODE;
    }

    public void setCODE_ENCODE(String CODE_ENCODE) {
        this.CODE_ENCODE = CODE_ENCODE;
    }

    /**
     * ===================================字符串处理结束===============//
     * ================================字符串编码转换开始===============//
     * 字符串编码转换为GBK
     *
     * @param str 被处理的字符串
     * @return String 将数据库中读出的数据进行转换
     */
    public static String convert(String str) {
        if (str == null)
            str = "";

        str = str.trim();
        if (str.length() > 0) {
            try {
                str = new String(str.getBytes("ISO_8859_1"), "GBK");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    /**
     * 字符串编码反转换为ISO8859_1
     *
     * @param str 被处理的字符串
     * @return String 将jsp提交的数据进行转换放入数据库中
     */
    public static String reconvert(String str) {
        if (str == null)
            str = "";
        str = str.trim();
        if (str.length() > 0) {
            try {
                str = new String(str.getBytes("GBK"), "ISO_8859_1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String utf8ConvertGbk(String str) {
        if (str == null)
            str = "";
        str = str.trim();
        if (str.length() > 0) {
            try {
                str = new String(str.getBytes(), "UTF-8");
                str = new String(str.getBytes("GBK"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String getCurDate() {
        java.util.Calendar c = java.util.Calendar.getInstance();
//        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy年MM月dd日hh时mm分ss秒");
        return (new java.text.SimpleDateFormat("yyyy-MM-dd")).format((java.util.Calendar.getInstance()).getTime());
    }

    /**
     * 从配置文件获取值
     *
     * @param key 配置文件中的键
     * @return 键值
     */
    public static String getPropertiesVal(String key) {
        String str = "";
        try {
            str = getMapByKey(DbTools.getInstance().getMapProperties(), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getMapByKey(Map mapParam, String key) {
        Object val = mapParam.get(key);
        if (val instanceof String)
            return val.toString();
        String retStr = "";
        if (val != null) {
            if (val instanceof Timestamp) //定义格式并转化成字符型日期格式
                retStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Timestamp(((Timestamp) val).getTime()));
            if (val instanceof java.sql.Date) //定义格式并转化成字符型日期格式
                retStr = (new SimpleDateFormat("yyyy-MM-dd")).format(val);
            if (val instanceof BigDecimal)
                retStr = val.toString();
        }
        return retStr;
    }

    public Map getMapProperties() {
        return mapProperties;
    }

    public void setMapProperties(Map mapProperties) {
        this.mapProperties = mapProperties;
    }

    public String getSQLMAP_NAMESPACE() {
        return SQLMAP_NAMESPACE;
    }

    public void setSQLMAP_NAMESPACE(String SQLMAP_NAMESPACE) {
        this.SQLMAP_NAMESPACE = SQLMAP_NAMESPACE;
    }

    public String getEDITOR() {
        return EDITOR;
    }

    public void setEDITOR(String EDITOR) {
        this.EDITOR = EDITOR;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public void setAUTHOR(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }

    public String getTABLE_ALIAS() {
        return TABLE_ALIAS;
    }

    public void setTABLE_ALIAS(String TABLE_ALIAS) {
        this.TABLE_ALIAS = TABLE_ALIAS;
    }

    public String getDaoPath() {
        return daoPath;
    }

    public void setDaoPath(String daoPath) {
        this.daoPath = daoPath;
    }

    public String getBoPath() {
        return boPath;
    }

    public void setBoPath(String boPath) {
        this.boPath = boPath;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }

    public String getSqlMapClientName() {
        return sqlMapClientName;
    }

    public void setSqlMapClientName(String sqlMapClientName) {
        this.sqlMapClientName = sqlMapClientName;
    }

    public String getTxTransactionProxyName() {
        return txTransactionProxyName;
    }

    public void setTxTransactionProxyName(String txTransactionProxyName) {
        this.txTransactionProxyName = txTransactionProxyName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        URL = url;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String user) {
        USER = user;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String pass) {
        PASS = pass;
    }

}

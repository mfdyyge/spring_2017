package yhj.tools.ibatis;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ibatis辅助工具
 * <ol>
 * Date:2008.10.23 editor:yanghongjian
 * <li>添加自动生成POJO注释-<u>只支持oracle</us>
 * <li>添加生成Dao接口及实现类
 * <li>添加生成Bo接口及实现类
 * <li>重构生成SQLMAP代码
 * </ol>
 * <ol>
 * Date: 2011-4-6  editor:yanghongjian
 * <li>SQLMAP、服务层代码和Web端代码文档
 * </ol>
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 * @since 1.4.1
 */
public class SqlMap_Oracle {
    class DDL {
        /**
         * 列的名称
         */
        String fieldName;
        /**
         * 列类型名
         */
        String fieldSqlType;
        /**
         * 生成POJO的属性
         */
        String javaAtrriber;
        /**
         * 属性的Java数据类型
         */
        String javaType;

        /**
         * 生成SqlMap时java别名 例如:javaType="string"
         */
        String sqlMapJavaType;
        /**
         * 生成SqlMap时SQL类型 例如:#COL0201:NUMERIC#
         */
        String sqlMapType;
        /**
         * 列注释
         */
        String fieldComments;
    }

    public SqlMap_Oracle() {
    }

    public static DbTools dbTools = DbTools.getInstance();
    private ArrayList al;
    private String tabelComments;
    private Map mapColcomments;
    private String namespace = "";
    private ArrayList priKey;

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
     * @param tableName
     */
    private void getTablePriKey(String tableName) {
        if (priKey == null)
            priKey = new ArrayList();
        else
            priKey.clear();
        String sql = "select col.column_name from user_constraints con,  user_cons_columns col where con.constraint_name = col.constraint_name " +
                "and con.constraint_type='P' and col.table_name = '" + tableName + "'";
        ResultSet rs = dbTools.getResultSet(sql);
        try {
            while (rs.next()) {
                priKey.add(rs.getString(1).toUpperCase());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取表、列的注释
     *
     * @param tableName 表名
     */
    private void getTableColComments(String tableName) {
        String sql = "select t.comments from user_tab_comments t where t.table_name=upper('" + tableName + "') ";
        ResultSet rs = dbTools.getResultSet(sql);
        String comm = "";
        try {
            rs.next();
            comm = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tabelComments = comm != null ? convert(comm) : "";

        sql = "select t.column_name, t.comments from user_col_comments t where t.table_name=upper('" + tableName + "')";
        rs = dbTools.getResultSet(sql);
        mapColcomments = new HashMap();
        try {
            while (rs.next())
                mapColcomments.put(rs.getString(1), convert(rs.getString(2)));
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void fillDllArrayList(String tableName) {
        //try{
        if (al == null)
            al = new ArrayList();
        else
            al.clear();
        try {
            getTableColComments(tableName);
            getTablePriKey(tableName);//取表主键

            String sql = "select * from " + tableName + " t where rownum = 1";
            ResultSet rs = dbTools.getResultSet(sql);

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                DDL ddl = new DDL();
                //获取指定列的名称
                ddl.fieldName = resultSetMetaData.getColumnName(i);
                ddl.javaAtrriber = ddl.fieldName;
                //检索列的特定于数据库的类型名
                ddl.fieldSqlType = resultSetMetaData.getColumnTypeName(i);
                if (ddl.fieldSqlType.equals("CHAR") || ddl.fieldSqlType.equals("VARCHAR2")) {//VARCHAR
                    ddl.javaType = "String";
                    ddl.sqlMapType = "VARCHAR";
                    ddl.sqlMapJavaType = "string";
                }
                if (ddl.fieldSqlType.equals("DATE")) {//DATE
                    ddl.javaType = "Date";
                    ddl.sqlMapType = "DATE";
                    ddl.sqlMapJavaType = "date";
                }
                if (ddl.fieldSqlType.equals("NUMBER")) {//NUMERIC
                    // ddl.javaType = "double";
                    ddl.javaType = "String";
                    ddl.sqlMapType = "NUMERIC";
                    // ddl.sqlMapJavaType = "double";
                    ddl.sqlMapJavaType = "string";
                }
                ddl.fieldComments = getFieldComments(ddl.fieldName);
                al.add(ddl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成与数据库表对应的持久化实体
     *
     * @param javaName 类名
     */
    public void createPojo(String javaName) {
        System.out.println("开始生成 " + javaName + " POJO...");
        fillDllArrayList(javaName);
        StringBuffer sb = new StringBuffer("import java.io.Serializable;\n");
        sb.append("//").append(tabelComments).append("实体\n\n");
        sb.append("public class ").append(javaName).append(" implements Serializable {\n");
        sb.append("\tpublic ").append(javaName).append("(){\n\t}\n\n");
        System.out.println("生成属性:");
        for (int i = 0; i < al.size(); i++) {
            DDL ddl = (DDL) al.get(i);
            sb.append("\t/**\n")
                    .append("\t *").append(ddl.fieldComments)
                    .append("\n\t */\n");
            sb.append("\tprivate ").append(ddl.javaType).append(' ').append(ddl.javaAtrriber);
            if (ddl.fieldSqlType.equals("NUMBER"))
                sb.append("=\"0.00\";\n");
            else if (ddl.fieldSqlType.equals("CHAR") || ddl.fieldSqlType.equals("VARCHAR2"))
                sb.append("=\"\";\n");
            else if (ddl.fieldSqlType.equals("DATE"))
                sb.append(";\n");
        }

        //生成get / set
        for (int i = 0; i < al.size(); i++) {
            DDL ddl = (DDL) al.get(i);
            sb.append("\n\t/**\n")
                    .append("\t * 获取").append(ddl.fieldComments)
                    .append("\n\t * @return 返回").append(ddl.fieldComments)
                    .append("\n\t */\n");
            sb.append("\tpublic ").append(ddl.javaType)
                    .append(" get").append(ddl.javaAtrriber.toUpperCase())
                    .append("(){\n");
            if (ddl.fieldSqlType.equals("NUMBER")) {
                sb.append("\t\tif (").append(ddl.javaAtrriber).append(".indexOf(\".\")<0){\n");
                sb.append("\t\t\treturn ").append(ddl.javaAtrriber).append("+\".00\";\n\t\t}");
                sb.append("\n\t\treturn this.").append(ddl.javaAtrriber).append(";\n\t}\n");

                sb.append("\n\t/**\n")
                        .append("\t * 设置").append(ddl.fieldComments)
                        .append("\n\t * @param ").append(ddl.javaAtrriber.toLowerCase()).append(" ")
                        .append(ddl.fieldComments)
                        .append("\n\t */\n");

                sb.append("\tpublic ").append("void set").append(ddl.javaAtrriber.toUpperCase())
                        .append("(").append(ddl.javaType).append(' ').append(ddl.javaAtrriber.toLowerCase())
                        .append("){\n");

                sb.append("\t\t this.").append(ddl.javaAtrriber).append("=").append(ddl.javaAtrriber.toLowerCase())
                        .append(";\n\t}\n");

            } else if (ddl.fieldSqlType.equals("CHAR") || ddl.fieldSqlType.equals("VARCHAR2")) {
                sb.append("\t\treturn this.").append(ddl.javaAtrriber).append(";\n\t}\n");

                sb.append("\n\t/**\n")
                        .append("\t * 设置").append(ddl.fieldComments)
                        .append("\n\t * @param ").append(ddl.javaAtrriber.toLowerCase()).append(" ")
                        .append(ddl.fieldComments)
                        .append("\n\t */\n");
                sb.append("\tpublic ").append("void set").append(ddl.javaAtrriber.toUpperCase())
                        .append("(").append(ddl.javaType).append(' ').append(ddl.javaAtrriber.toLowerCase())
                        .append("){\n");

                sb.append("\t\t this.").append(ddl.javaAtrriber).append("=").append(ddl.javaAtrriber.toLowerCase())
                        .append(";\n\t}\n");
            } else {
                sb.append("\t\treturn this.").append(ddl.javaAtrriber).append(";\n\t}\n");

                sb.append("\n\t/**\n")
                        .append("\t * 设置").append(ddl.fieldComments)
                        .append("\n\t * @param ").append(ddl.javaAtrriber.toLowerCase()).append(" ")
                        .append(ddl.fieldComments)
                        .append("\n\t */\n");
                sb.append("\tpublic ").append("void set").append(ddl.javaAtrriber.toUpperCase())
                        .append("(").append(ddl.javaType).append(' ').append(ddl.javaAtrriber.toLowerCase())
                        .append("){\n");

                sb.append("\t\t this.").append(ddl.javaAtrriber).append("=").append(ddl.javaAtrriber.toLowerCase())
                        .append(";\n\t}\n");
            }
        }

        sb.append("}\n");
        String fileName = "c:\\" + javaName + ".java";
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");
    }

    /**
     * 生成添加sql
     *
     * @param sqlName 表名
     * @return sql字符串
     */
    private StringBuffer createSqlMapXmlInsert(String sqlName) {
        DDL ddl;
        StringBuffer sb = new StringBuffer();
        sb.append("\t\tINSERT INTO ").append(sqlName).append("(");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size())) {
                sb.append(ddl.fieldName).append(",\n\t\t\t");
            } else {
                sb.append(ddl.fieldName).append(',');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")\n" +
                "\t\t\tVALUES(");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);

//            sb.append("\t\t\t\t\t<isNotEmpty prepend=\",\" property=\"").append(ddl.fieldName).append("\">\n\t\t\t\t\t\t");
//            if("DATE".equals(ddl.fieldSqlType)){
//            	sb.append("TO_DATE(#").append(ddl.javaAtrriber).append("#,'yyyy-mm-dd')");
//            }else{
//            	sb.append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#");
//            }
//            sb.append("\n\t\t\t\t\t</isNotEmpty>\n");
            String tem = "";
            if ("DATE".equals(ddl.fieldSqlType)) {
                tem = "TO_DATE(#" + ddl.javaAtrriber + "#,'yyyy-mm-dd')";
            } else {
                tem = "#" + ddl.javaAtrriber + ":" + ddl.sqlMapType + "#";
            }

            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append(tem).append(",\n\t\t\t");
            else
                sb.append(tem).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")\n");
        System.out.println("生成添加(INSERT)SQL......");
        return sb;
    }

    private StringBuffer createSqlMapXmlUpdate(String sqlName) {
        StringBuffer sb = new StringBuffer();
        sb.append("\t\tUPDATE ").append(sqlName);
//        sb.append("\n\t\t\t\t<dynamic prepend=\"SET\">\n");
        sb.append("  SET\n\t\t\t");
        for (int i = 0; i < al.size(); i++) {
            DDL ddl = (DDL) al.get(i);
//            sb.append("\t\t\t\t\t<isNotEmpty prepend=\",\" property=\"").append(ddl.fieldName).append("\">")
//                    .append(ddl.fieldName).append(" = #").append(ddl.javaAtrriber).append("#</isNotEmpty>\n");
            int rel = i + 1;
            if (rel != al.size()) {
                if ("DATE".equals(ddl.fieldSqlType))
                    sb.append(ddl.fieldName).append("=").append("TO_DATE(#").append(ddl.javaAtrriber).append("#,'yyyy-mm-dd'),\n\t\t\t");
                else
                    sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,\n\t\t\t");
            } else {
                if ("DATE".equals(ddl.fieldSqlType))
                    sb.append(ddl.fieldName).append("=").append("TO_DATE(#").append(ddl.javaAtrriber).append("#,'yyyy-mm-dd')\n");
                else
                    sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#\n");
            }
        }
        //sb.deleteCharAt(sb.length() - 1);
//        sb.append("\n\t\t\t\t</dynamic>");
        sb.append("\t\tWHERE ");
        for (int i = 0; i < priKey.size(); i++) {
            sb.append(priKey.get(i)).append(" = ").append("#").append(priKey.get(i)).append("#");
            if (i != priKey.size() - 1)
                sb.append(" AND ");
        }
        System.out.println("生成更新(UPDATA)SQL...");
        return sb;
    }

    /**
     * 动态更新语句
     *
     * @param sqlName
     * @return
     */
    private StringBuffer createSqlMapXmlUpdateSelective(String sqlName) {
        StringBuffer sb = new StringBuffer();
        sb.append("\t\tUPDATE ").append(sqlName);
//        sb.append("\n\t\t\t\t<dynamic prepend=\"SET\">\n");
        //sb.append("  SET\n");
        sb.append("\n\t\t<dynamic prepend=\"set\">\n");
        for (int i = 0; i < al.size(); i++) {
            DDL ddl = (DDL) al.get(i);
            sb.append("\t\t\t<isNotNull prepend=\",\" property=\"").append(ddl.fieldName).append("\">")
                    .append(ddl.fieldName).append(" = #").append(ddl.javaAtrriber).append("#</isNotNull>\n");
            //int rel = i + 1;
            //if (rel != al.size()){
//            	if("DATE".equals(ddl.fieldSqlType))
//            		sb.append(ddl.fieldName).append("=").append("TO_DATE(#").append(ddl.javaAtrriber).append("#,'yyyy-mm-dd')\n\t\t\t");
//            	else
//            		sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#\n\t\t\t");
//            } else{
//            	if("DATE".equals(ddl.fieldSqlType))
//            		sb.append(ddl.fieldName).append("=").append("TO_DATE(#").append(ddl.javaAtrriber).append("#,'yyyy-mm-dd')\n");
//            	else
//            		sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#\n");
//            }
        }
        sb.append("\t\t</dynamic>\n");
        //sb.deleteCharAt(sb.length() - 1);
        //sb.append("\n\t\t\t\t</dynamic>");
        sb.append("\t\tWHERE ");
        for (int i = 0; i < priKey.size(); i++) {
            sb.append(priKey.get(i)).append(" = ").append("#").append(priKey.get(i)).append("#");
            if (i != priKey.size() - 1)
                sb.append(" AND ");
        }
        System.out.println("生成更新(UPDATA)SQL...");
        return sb;
    }

    /**
     * 生成删除sql
     *
     * @param sqlName 表名
     * @return 返回del sql
     */
    private StringBuffer createSqlMapXmlDel(String sqlName) {
        StringBuffer sb = new StringBuffer();
        // sb.append("\t<delete id=\"del").append(sqlName).append("\" parameterClass=\"map\">\n");
        sb.append("\t\tDELETE FROM ").append(sqlName).append(" " + dbTools.getTABLE_ALIAS());
        sb.append(" WHERE ");
        for (int i = 0; i < priKey.size(); i++) {
            sb.append(dbTools.getTABLE_ALIAS() + "." + priKey.get(i)).append(" = ").append("#").append(priKey.get(i)).append("#");
            if (i != priKey.size() - 1)
                sb.append(" AND ");
        }


        //sb.append("\n\t\t<include refid=\"" + sqlName + "_WHERE\"/>");
//        sb.append("\n\t\t<dynamic prepend=\"WHERE\">");
//        DDL ddl;
//        for (int i = 0; i < al.size(); i++) {
//            ddl = (DDL) al.get(i);
//            sb.append("\n\t\t\t").append("<isNotEmpty prepend = \"and\" property= \"" + ddl.fieldName + "\">");
//            sb.append("\n\t\t\t\t").append(ddl.fieldName).append("=").append("#" + ddl.fieldName + "#");
//            sb.append("\n\t\t\t</isNotEmpty>");
//        }
//        sb.append("\n\t\t</dynamic>");
        //sb.append("\n\t</delete>\n\n");
        System.out.println("生成删除(DELETE)SQL...");
        return sb;
    }

    /**
     * 生成ibatis的sqlMap文档
     *
     * @param sqlName sqlMap文档的名称
     */
    public void createSqlMapXml(String sqlName, String javaName) {
        System.out.println("开始生成" + sqlName + "sqlMap文档...");
        StringBuffer sb = new StringBuffer("");
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE sqlMap PUBLIC \"-//ibatis.apache.org//DTD SQL Map 2.0//EN\"\n" +
                "    \"http://ibatis.apache.org/dtd/sql-map-2.dtd\">\n";
        sb.append(str);
//        String namespace = DbTools.getInstance().getSQLMAP_NAMESPACE();
//        namespace = namespace.length() > 0 ? namespace : sqlName;

        sb.append("<sqlMap namespace=\"").append(namespace).append("\">\n");
        sb.append("\t<!--" + tabelComments + "(" + sqlName + ")对应的sqlmap-->\n");
        sb.append("    <cacheModel id=\"cache" + sqlName + "\" type=\"LRU\">\n" +
                "        <flushInterval hours=\"24\"/>\n" +
                "        <flushOnExecute statement=\"" + namespace + ".insert" + sqlName + "\"/>\n" +
                "        <flushOnExecute statement=\"" + namespace + ".update" + sqlName + "\"/>\n" +
                "        <flushOnExecute statement=\"" + namespace + ".save" + sqlName + "\"/>\n" +
                "        <flushOnExecute statement=\"" + namespace + ".del" + sqlName + "\"/>\n" +
                "        <property name=\"size\" value=\"50\"/>\n" +
                "    </cacheModel>\n\n");
        DDL ddl;
        /* System.out.println("生成resultMap...");

            sb.append("\t<resultMap id=\"result").append(sqlName).append("\" class=\"").append(sqlName.toLowerCase()).append("\">\n");

          for (int i = 0; i < al.size(); i++) {
              ddl = (DDL) al.get(i);
              if (ddl.fieldSqlType.equals("NUMBER")) {
                  sb.append("\t\t<result property=\"" + ddl.javaAtrriber + "\" javaType=\"" + ddl.sqlMapJavaType
                          + "\" column=\"" + ddl.fieldName + "\" jdbcType=\"" + ddl.sqlMapType + "\" nullValue=\"0.00\"/>\n");
              } else if (ddl.fieldSqlType.equals("CHAR") || ddl.fieldSqlType.equals("VARCHAR2")) {
                  sb.append("\t\t<result property=\"" + ddl.javaAtrriber + "\" javaType=\"" + ddl.sqlMapJavaType
                          + "\" column=\"" + ddl.fieldName + "\" jdbcType=\"" + ddl.sqlMapType + "\" nullValue=\"\"/>\n");
              } else if (ddl.fieldSqlType.equals("DATE")) {
                  sb.append("\t\t<result property=\"" + ddl.javaAtrriber + "\" javaType=\"" + ddl.sqlMapJavaType
                          + "\" column=\"" + ddl.fieldName + "\" jdbcType=\"" + ddl.sqlMapType + "\"/>\n");
              } else {
                  sb.append("\t\t<result property=\"" + ddl.javaAtrriber + "\" javaType=\"" + ddl.sqlMapJavaType
                          + "\" column=\"" + ddl.fieldName + "\" jdbcType=\"" + ddl.sqlMapType + "\" nullValue=\"\"/>\n");
              }
          }
          sb.append("\t</resultMap>\n");
        */

        System.out.println("生成查询条件SQL...");
        sb.append("\t<!--" + tabelComments + "(" + sqlName + ")通用查询条件-->\n");
        sb.append("\t<sql id=\"" + sqlName + "_WHERE\">\n");
        sb.append("\t\t<dynamic prepend=\"WHERE\">");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            sb.append("\n\t\t\t").append("<isNotEmpty prepend=\"and\" property=\"" + ddl.fieldName + "\">");
            if ("DATE".equals(ddl.fieldSqlType))
                sb.append("\n\t\t\t\t").append(dbTools.getTABLE_ALIAS() + "." + ddl.fieldName).append(" = ").append("TO_DATE(#").append(ddl.fieldName).append("#,'yyyy-mm-dd')");
            else
                sb.append("\n\t\t\t\t").append(dbTools.getTABLE_ALIAS() + "." + ddl.fieldName).append(" = ").append("#" + ddl.fieldName + "#");
            sb.append("\n\t\t\t</isNotEmpty>");
        }
        sb.append("\n\t\t</dynamic>\n");
        sb.append("\t</sql>\n\n");

        System.out.println("生成查找SQL...");
        sb.append("\t<select id=\"get").append(sqlName).append("\" parameterClass=\"map\" resultClass=\"java.util.HashMap\" cacheModel=\"cache").append(sqlName.toUpperCase()).append("\">\n");

        //添加分页
        sb.append("\t\t<isNotNull  property=\"END\">\n");
        sb.append("\t\tSELECT F.* FROM (\n")
                .append("\t\t</isNotNull>\n")
                .append("\t\tSELECT E.*,ROWNUM ROWNO FROM (\n");

        sb.append("\t\t\tSELECT ");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            int rel = i + 1;
            String field = dbTools.getTABLE_ALIAS() + "." + ddl.fieldName;
            if (ddl.fieldSqlType.equals("DATE"))
                field = "TO_CHAR(" + dbTools.getTABLE_ALIAS() + "." + ddl.fieldName + ",'yyyy-mm-dd') " + ddl.fieldName;
//                field = "TO_CHAR("+ddl.fieldName+",'yyyy-mm-dd hh24:mi:ss') "+ddl.fieldName;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append(field).append(",\n\t\t\t\t");
            else
                sb.append(field).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        sb.append("\t\t\tFROM ").append(sqlName + " " + dbTools.getTABLE_ALIAS()).append('\n');
        sb.append("\t\t\t<include refid=\"" + sqlName + "_WHERE\"/>\n");
        sb.append("\t\t)E\n")
                .append("\t\t<isNotNull  property=\"END\">\n")
                .append("\t\t\t<![CDATA[ WHERE rownum <=#END# ) F WHERE F.ROWNO >=#START#]]>\n")
                .append("\t\t</isNotNull>");
        sb.append("\n\t</select>\n\n");

        System.out.println("生成Count SQL...");
        sb.append("\t<select id=\"get").append(sqlName).append("Count\" parameterClass=\"map\" resultClass=\"java.lang.Integer\" cacheModel=\"cache").append(sqlName.toUpperCase()).append("\">\n");
        sb.append("\t\tSELECT COUNT(1) FROM ").append(sqlName + " " + dbTools.getTABLE_ALIAS()).append('\n');
        sb.append("\t\t<include refid=\"" + sqlName + "_WHERE\"/>");
        sb.append("\n\t</select>\n\n");

        System.out.println("生成添加SQL....");

        sb.append("\t<insert id=\"insert" + sqlName + "\" parameterClass=\"map\">\n")
                .append(createSqlMapXmlInsert(sqlName)).append("\t</insert>\n");//添加记录
        //更新记录
//        sb.append("\n\t<update id=\"update" + sqlName + "\" parameterClass=\"map\">\n")
//                .append(createSqlMapXmlUpdate(sqlName)).append("\n\t</update>\n");
        //更新记录 动态更新  Dynamic
        sb.append("\n\t<update id=\"update" + sqlName + "\" parameterClass=\"map\">\n")
                .append(createSqlMapXmlUpdateSelective(sqlName)).append("\n\t</update>\n");

        sb.append("\n\t<delete id=\"del" + sqlName + "\" parameterClass=\"map\">\n")
                .append(createSqlMapXmlDel(sqlName)).append("\n\t</delete>\n");//删除记录


        sb.append("\t<insert id=\"save" + sqlName + "\" parameterClass=\"map\">\n" +
                " \t\t<![CDATA[\n" +
                "\t\tDECLARE\n" +
                "\t\t\tn_count number(1);\n" +
                "\t\tBEGIN\n" +
                "\t\t\tSELECT count(1) INTO n_count FROM " + sqlName + "\n" +
                "\t\t\tWHERE ");
        for (int i = 0; i < priKey.size(); i++) {
            sb.append(priKey.get(i)).append(" = ").append("#").append(priKey.get(i)).append("#");
            if (i != priKey.size() - 1)
                sb.append(" AND ");
        }
        sb.append(";\n\t\t\tIF n_count = 0 THEN\n")
                .append("\t\t" + createSqlMapXmlInsert(sqlName))//添加记录
                .append("\t\t\tELSE\n")
                .append("\t\t" + createSqlMapXmlUpdateSelective(sqlName))//更新记录
                .append("\n\t\t\tEND IF;\n\t\tEND;")
                .append("\n\t\t]]>")
                .append("\n\t</insert>\n\n");
        sb.append("</sqlMap>\n");


        String strFilePath = dbTools.getCODE_DIR() + File.separator + javaName + File.separator;
        File filePath = new File(strFilePath);
        if (!filePath.exists()) {
            filePath.mkdir();
            System.out.println("生成代码目录:" + strFilePath);
        }


        String fileName = strFilePath + sqlName + ".xml";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");
    }

    /**
     * 生成持久化实体的操作类
     *
     * @param javaName 类名
     */
    public void createDao(String javaName, String sqlName) {
        System.out.println("开始生成 " + javaName + " Dao数据操作类...");
        String daoClass = "IDao" + javaName;
        String daoClassImpl = "Dao" + javaName + "Impl";

        String desc1 = "package " + dbTools.getDaoPath() + ";\n\n" +
                "import base.service.dao.IDao;\n" +
                "import base.service.domain.exception.DaoException;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "/**\n" +
                " * <ol>\n" +
                " * date:" + DbTools.getCurDate() + " editor:" + dbTools.getEDITOR() + "\n" +
                " * <li>创建文档</li>\n" +
                " * <li>" + tabelComments + "数据操作接口类</li>\n" +
                " * </ol>\n" +
                " * <ol>\n" +
                " *\n";
        if (!"".equals(dbTools.getAUTHOR()))
            desc1 += " * @author <a href=\"" + dbTools.getAUTHOR() + "\">" + dbTools.getEDITOR() + "</a>\n";
        else
            desc1 += " * @author " + dbTools.getEDITOR() + "\n";
        desc1 += " * @version 2.0\n" +
                " * @since 1.6\n" +
                " */\n";
        String desc2 = "package " + dbTools.getDaoPath() + ".impl;\n\n" +
                "import base.service.dao.BaseDao;\n" +
                "import base.service.domain.exception.DaoException;\n" +
                "import com.ibatis.sqlmap.client.SqlMapClient;\n" +
                "import org.apache.log4j.Logger;\n" +
                "import java.sql.SQLException;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import " + dbTools.getDaoPath() + "." + daoClass + ";\n" +
                "/**\n" +
                " * <ol>\n" +
                " * date:" + DbTools.getCurDate() + " editor:" + dbTools.getEDITOR() + "\n" +
                " * <li>创建文档</li>\n" +
                " * <li>" + tabelComments + "数据操作接口实现类</li>\n" +
                " * </ol>\n" +
                " * <ol>\n" +
                " *\n";
        if (!"".equals(dbTools.getAUTHOR()))
            desc2 += " * @author <a href=\"" + dbTools.getAUTHOR() + "\">" + dbTools.getEDITOR() + "</a>\n";
        else
            desc2 += " * @author " + dbTools.getEDITOR() + "\n";
        desc2 += " * @version 2.0\n" +
                " * @since 1.6\n" +
                " */\n";

        StringBuffer sb = new StringBuffer(desc1);
        StringBuffer sbImpl = new StringBuffer(desc2);

        sb.append("public interface ").append(daoClass).append(" extends IDao {\n");
        sbImpl.append("public final class ").append(daoClassImpl).append(" extends BaseDao implements ").append(daoClass).append(" {\n");
        sbImpl.append("\tprivate Logger log = Logger.getLogger(this.getClass());\n\n");
        sbImpl.append("\tpublic ").append(daoClassImpl).append("(SqlMapClient sqlMap){\n\t\tsuper(sqlMap);")
                .append("\n\t}\n\n");

        String method, comment;
        System.out.println("生成添加方法.......");
        comment = "\t/**\n\t * 添加" + tabelComments + "记录\n"
                + "\t * @param paramMap " + tabelComments + "map对象集合\n"
                + "\t * @return 返回成功标志 1为成功 0为不成功"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int insert" + javaName + "(Map paramMap) throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int insert" + javaName + "(Map paramMap) throws DaoException{" +
                "\n\t\ttry {" +
                "\n\t\t\tsqlMap.insert(\"" + namespace + ".insert" + sqlName + "\", paramMap);" +
                "\n\t\t} catch (SQLException e) {" +
                "\n\t\t\tString err = \"添加出错->出错原因:\" + e.getMessage();" +
                "\n\t\t\tlog.error(err, e);" +
                "\n\t\t\tthrow new DaoException(err, e);" +
                "\n\t\t}" +
//                " finally {" +
//                "\n\t\t\ttry {" +
//                "\n\t\t\t\tsqlMap.endTransaction();" +
//                "\n\t\t\t} catch (SQLException e) {" +
//                "\n\t\t\t\tlog.error(e.getMessage());" +
//                "\n\t\t\t}" +
//                "\n\t\t}" +
                "\n\t\treturn 1;\n\t}\n\n";
        sbImpl.append(method);
//        comment = "\t/**\n\t * 修改" + tabelComments + "记录\n"
//                + "\t * @param paramMap " + tabelComments + "map对象集合\n"
//                + "\t * @return 返回成功标志 1为成功 0为不成功"
//                + "\n\t * @throws DaoException 出错"
//                + "\n\t */\n";
//        method = comment + "\tpublic int update" + javaName + "(Map paramMap) throws DaoException;\n\n";
//        sb.append(method);
//        method = comment + "\tpublic int update" + javaName + "(Map paramMap) throws DaoException{" +
//                "\n\t\ttry {" +
////                "\n\t\t\tsqlMap.startTransaction();" +
//                //"\n\t\t\tsqlMap.startBatch();" +
//                //"\n\t\t\tfor (Iterator it = listMap.iterator(); it.hasNext(); ) {" +
//                //"\n\t\t\tmapCopy(map" + javaName + "Cols, paramMap);" +
//                "\n\t\t\tsqlMap.update(\"" + namespace + ".update" + sqlName + "\", paramMap);" +
//                //"\n\t\t\t}" +
////                "\n\t\t\tsqlMap.update(\""+namespace+".save" + javaName + "\", mapParam);" +
//                //"\n\t\t\tsqlMap.executeBatch();" +
////                "\n\t\t\tsqlMap.commitTransaction();" +
//                "\n\t\t} catch (SQLException e) {" +
//                "\n\t\t\tString err = \"修改出错->出错原因:\" + e.getMessage();" +
//                "\n\t\t\tlog.error(err, e);" +
//                "\n\t\t\tthrow new DaoException(err, e);" +
//                "\n\t\t}" +
////                " finally {" +
////                "\n\t\t\ttry {" +
////                "\n\t\t\t\tsqlMap.endTransaction();" +
////                "\n\t\t\t} catch (SQLException e) {" +
////                "\n\t\t\t\tlog.error(e.getMessage());" +
////                "\n\t\t\t}" +
////                "\n\t\t}" +
//                "\n\t\treturn 1;\n\t}\n\n";
//        sbImpl.append(method);


        comment = "\t/**\n\t * 动态修改" + tabelComments + "记录\n"
                + "\t * @param paramMap " + tabelComments + "map对象集合\n"
                + "\t * @return 返回成功标志 1为成功 0为不成功"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int update" + javaName + "(Map paramMap) throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int update" + javaName + "(Map paramMap) throws DaoException{" +
                "\n\t\ttry {" +
                "\n\t\t\tsqlMap.update(\"" + namespace + ".update" + sqlName + "\", paramMap);" +
                "\n\t\t} catch (SQLException e) {" +
                "\n\t\t\tString err = \"修改出错->出错原因:\" + e.getMessage();" +
                "\n\t\t\tlog.error(err, e);" +
                "\n\t\t\tthrow new DaoException(err, e);" +
                "\n\t\t}" +
                "\n\t\treturn 1;\n\t}\n\n";
        sbImpl.append(method);

        comment = "\t/**\n\t * 保存" + tabelComments + "记录(没有记录就添加,否则就更新)\n"
                + "\t * @param paramMap " + tabelComments + "map对象集合\n"
                + "\t * @return 返回成功标志 1为成功 0为不成功"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int save" + javaName + "(Map paramMap) throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int save" + javaName + "(Map paramMap) throws DaoException{" +
                "\n\t\ttry {" +

                "\n\t\t\tsqlMap.update(\"" + namespace + ".save" + javaName + "\", paramMap);" +
                "\n\t\t} catch (SQLException e) {" +
                "\n\t\t\tString err = \"成批添加或更新出错->出错原因:\" + e.getMessage();" +
                "\n\t\t\tlog.error(err, e);" +
                "\n\t\t\tthrow new DaoException(err, e);" +
                "\n\t\t}" +
                "\n\t\treturn 1;\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成查询方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录\n"
                + "\t * @param mapParam " + tabelComments + "查询条件\n"
                + "\t * @return 返回" + tabelComments + "map对象集合,否则返回空集合"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic List get" + javaName + "(Map mapParam) throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic List get" + javaName + "(Map mapParam) throws DaoException{" +
                // "\n\t\tMapRowHandler mapRow = new MapRowHandler();" +
                "\n\t\tList list;" +
                "\n\t\ttry {" +
                "\n\t\t\tlist = sqlMap.queryForList(\"" + namespace + ".get" + sqlName + "\", mapParam != null ? mapParam : new HashMap());" +
                "\n\t\t} catch (Exception e) {" +
                "\n\t\t\tString err = \"查询出错->出错原因:\" + e.getMessage();" +
                "\n\t\t\tlog.error(err, e);" +
                "\n\t\t\tthrow new DaoException(err, e);" +
                "\n\t\t}" +
                "\n\t\treturn list;\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成统计方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录数\n"
                + "\t * @param mapParam " + tabelComments + "查询条件\n"
                + "\t * @return 返回" + tabelComments + "记录数"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int get" + javaName + "Count(Map mapParam) throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int get" + javaName + "Count(Map mapParam) throws DaoException{" +
                "\n\t\tInteger count;" +
                "\n\t\ttry {" +
                "\n\t\t\tcount = (Integer)sqlMap.queryForObject(\"" + namespace + ".get" + sqlName + "Count\", mapParam != null ? mapParam : new HashMap());" +
                "\n\t\t} catch (Exception e) {" +
                "\n\t\t\tString err = \"查询出错->出错原因:\" + e.getMessage();" +
                "\n\t\t\tlog.error(err, e);" +
                "\n\t\t\tthrow new DaoException(err, e);" +
                "\n\t\t}" +
                "\n\t\treturn count.intValue();\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成删除方法.......");
        comment = "\t/**\n\t * 删除" + tabelComments + "记录\n"
                + "\t * @param paramMap " + tabelComments + "删除map对象集合\n"
                + "\t * @return 返回操作影响数 0为不成功"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(Map paramMap) throws DaoException;";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(Map paramMap) throws DaoException{" +
                "\n\t\tint count;" +
                "\n\t\ttry {" +
//                "\n\t\t\tsqlMap.startTransaction();" +
                //"\n\t\t\tsqlMap.startBatch();" +
                //"\n\t\t\tfor (Iterator it = listMap.iterator(); it.hasNext(); ) {" +
                "\n\t\t\tcount = sqlMap.delete(\"" + namespace + ".del" + sqlName + "\", paramMap);" +
                //"\n\t\t\t}" +
                //"\n\t\t\tsqlMap.executeBatch();" +
//                "\n\t\t\tsqlMap.commitTransaction();" +
                "\n\t\t} catch (SQLException e) {" +
                "\n\t\t\tString err = \"成批删除-->出错原因:\" + e.getMessage();" +
                "\n\t\t\tlog.error(err, e);" +
                "\n\t\t\tthrow new DaoException(err, e);" +
                "\n\t\t} " +
//                "finally {" +
//                "\n\t\t\ttry {" +
//                "\n\t\t\t\tsqlMap.endTransaction();" +
//                "\n\t\t\t} catch (SQLException e) {" +
//                "\n\t\t\t\tlog.error(e.getMessage());" +
//                "\n\t\t\t}" +
//                "\n\t\t}" +
                "\n\t\treturn count;\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");

        String strFilePath = dbTools.getCODE_DIR() + File.separator + javaName + File.separator;
        File filePath = new File(strFilePath);
        if (!filePath.exists()) {
            filePath.mkdir();
            System.out.println("生成代码目录:" + strFilePath);
        }

        String fileName = strFilePath + daoClass + ".java";
        String fileNameImple = strFilePath + daoClassImpl + ".java";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            OutputStreamWriter writerImpl = new OutputStreamWriter(
                    new FileOutputStream(fileNameImple), dbTools.getCODE_ENCODE());
            writerImpl.write(sbImpl.toString());
            writerImpl.flush();
            writerImpl.close();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");
    }

    /**
     * 生成服务层的操作类
     *
     * @param javaName 类名
     */
    public void createBo(String javaName) {
        System.out.println("开始生成 " + javaName + " Bo业务处理类...");
        String boClass = "IBo" + javaName;
        String boClassImpl = "Bo" + javaName + "Impl";

        String desc1 = "package " + dbTools.getBoPath() + ";\n\n" +
                "import base.service.bo.IBo;\n" +
                "import base.service.domain.exception.DelErrorException;\n" +
                "import base.service.domain.exception.SaveException;\n" +
                "import java.util.List;\n" +
                "import java.util.Iterator;\n" +
                "import java.util.Map;\n" +
                "import base.service.domain.tools.JsonHelp;\n" +
                "/**\n" +
                " * <ol>\n" +
                " * date:" + DbTools.getCurDate() + " editor:" + dbTools.getEDITOR() + "\n" +
                " * <li>创建文档</li>\n" +
                " * <li>" + tabelComments + "业务处理接口类</li>\n" +
                " * </ol>\n" +
                " * <ol>\n" +
                " *\n";
        if (!"".equals(dbTools.getAUTHOR()))
            desc1 += " * @author <a href=\"" + dbTools.getAUTHOR() + "\">" + dbTools.getEDITOR() + "</a>\n";
        else
            desc1 += " * @author " + dbTools.getEDITOR() + "\n";
        desc1 += " * @version 2.0\n" +
                " * @since 1.6\n" +
                " */\n";
        String desc2 = "package " + dbTools.getBoPath() + ".impl;\n\n" +
                "import base.service.dao.IDao;\n" +
                "import base.service.domain.exception.DaoException;\n" +
                "import base.service.domain.exception.DelErrorException;\n" +
                "import base.service.domain.exception.SaveException;\n" +
                "import base.service.domain.exception.ServiceLocatorException;\n" +
                "import org.apache.log4j.Logger;\n" +
                "import java.util.ArrayList;\n" +
                //"import java.util.Iterator;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import " + dbTools.getBoPath() + "." + boClass + ";\n" +
                "import " + dbTools.getDaoPath() + "." + "IDao" + javaName + ";\n" +
                "/**\n" +
                " * <ol>\n" +
                " * date:" + DbTools.getCurDate() + " editor:" + dbTools.getEDITOR() + "\n" +
                " * <li>创建文档</li>\n" +
                " * <li>" + tabelComments + "业务处理接口实现类</li>\n" +
                " * </ol>\n" +
                " * <ol>\n" +
                " *\n";
        if (!"".equals(dbTools.getAUTHOR()))
            desc2 += " * @author <a href=\"" + dbTools.getAUTHOR() + "\">" + dbTools.getEDITOR() + "</a>\n";
        else
            desc2 += " * @author " + dbTools.getEDITOR() + "\n";
        desc2 += " * @version 2.0\n" +
                " * @since 1.6\n" +
                " */\n";

        StringBuffer sb = new StringBuffer(desc1);
        StringBuffer sbImpl = new StringBuffer(desc2);

        sb.append("public interface ").append(boClass).append(" extends IBo {\n");
        sbImpl.append("public class ").append(boClassImpl).append(" implements ").append(boClass).append(" {\n");
        sbImpl.append("\tprivate Logger log = Logger.getLogger(this.getClass());\n");
        //sbImpl.append("\tprivate int iRet = 0;\n");
        //sbImpl.append("\tprivate List list = new ArrayList();\n");
        sbImpl.append("\tprivate IDao").append(javaName).append(" iDao;\n");
        sbImpl.append("\n\tpublic ").append(boClassImpl).append("(IDao iDao){")
                .append("\n\t\tthis.iDao = (IDao" + javaName + ")iDao;")
                .append("\n\t}\n\n");

        String method, comment;
        System.out.println("生成保存方法.......");
        comment = "\t/**\n\t * 保存" + tabelComments + "记录\n"
                + "\t * @param mapParam " + tabelComments + "map对象集合\n"
                + "\t * @return 返回操作影响数 0为不成功"
                + "\n\t * @throws SaveException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int insert" + javaName + "(Map mapParam) throws SaveException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int insert" + javaName + "(Map mapParam) throws SaveException{";
        for (int i = 0; i < priKey.size(); i++) {
            method += "\n\t\tif (JsonHelp.isEmpty(mapParam.get(\"" + priKey.get(i) + "\")))\n" +
                    "            throw new SaveException(\"" + mapColcomments.get(priKey.get(i)) + "不允许为空!\");\n";
        }
        method += "\n\t\tMap mapTem = new HashMap();\n";
        for (int i = 0; i < priKey.size(); i++) {
            method += "        mapTem.put(\"" + priKey.get(i) + "\",JsonHelp.getMapByKey(mapParam,\"" + priKey.get(i) + "\");";
        }

        method += "\n\t\ttry {\n" +
                //"            //for(Iterator it = listMap.iterator(); it.hasNext(); ){\n" +
                "            if(iDao.get" + javaName + "(mapTem).size()>0)\n" +
                "                throw new SaveException(\"已经存在不允许添加!\");\n\n" +
                "            return iDao.insert" + javaName + "(mapParam);\n" +
                //"           // }\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new SaveException(e.getMessage(), e);\n" +
                "        }" +
                //"\n\t\treturn 1;" +
                "\n\t}\n\n";
        sbImpl.append(method);

//        System.out.println("生成修改方法.......");
//        comment = "\t/**\n\t * 修改" + tabelComments + "记录\n"
//                + "\t * @param mapParam " + tabelComments + "map对象集合\n"
//                + "\t * @return 返回操作影响数 0为不成功"
//                + "\n\t * @throws SaveException 出错"
//                + "\n\t */\n";
//        method = comment + "\tpublic int update" + javaName + "(Map mapParam) throws SaveException;\n\n";
//        sb.append(method);
//        method = comment + "\tpublic int update" + javaName + "(Map mapParam) throws SaveException{" +
//                "\n\t\ttry{\n" +
//                //"          for(Iterator it = listMap.iterator(); it.hasNext(); ){\n" +
//                "            return iDao.update" + javaName + "(mapParam);\n" +
//                //"          }\n" +
//                "        } catch (DaoException e) {\n" +
//                "            log.error(e.getMessage());\n" +
//                "            throw new SaveException(e.getMessage(), e);\n" +
//                "        }" +
//                //"\n\t\treturn listMap.size();" +
//                "\n\t}\n\n";
//        sbImpl.append(method);

        System.out.println("生成动态修改方法.......");
        comment = "\t/**\n\t * 动态修改" + tabelComments + "记录\n"
                + "\t * @param mapParam " + tabelComments + "map对象集合\n"
                + "\t * @return 返回操作影响数 0为不成功"
                + "\n\t * @throws SaveException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int update" + javaName + "(Map mapParam) throws SaveException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int update" + javaName + "(Map mapParam) throws SaveException{";
        for (int i = 0; i < priKey.size(); i++) {
            method += "\n\t\tif (JsonHelp.isEmpty(mapParam.get(\"" + priKey.get(i) + "\")))\n" +
                    "            throw new SaveException(\"" + mapColcomments.get(priKey.get(i)) + "不允许为空!\");\n";
        }
        method += "\n\t\ttry{\n" +
                //"          for(Iterator it = listMap.iterator(); it.hasNext(); ){\n" +
                "            return iDao.update" + javaName + "(mapParam);\n" +
                //"          }\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new SaveException(e.getMessage(), e);\n" +
                "        }" +
                //"\n\t\treturn listMap.size();" +
                "\n\t}\n\n";
        sbImpl.append(method);

        method = comment + "\tpublic int save" + javaName + "(Map mapParam) throws SaveException;\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 保存" + tabelComments + "记录(没有记录就添加,否则就更新)\n"
                + "\t * @param paramMap " + tabelComments + "map对象集合\n"
                + "\t * @return 返回成功标志 1为成功 0为不成功"
                + "\n\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int save" + javaName + "(Map mapParam) throws SaveException{";
        for (int i = 0; i < priKey.size(); i++) {
            method += "\n\t\tif (JsonHelp.isEmpty(mapParam.get(\"" + priKey.get(i) + "\")))\n" +
                    "            throw new SaveException(\"" + mapColcomments.get(priKey.get(i)) + "不允许为空!\");\n";
        }
        method += "\n\t\ttry{\n" +
                "            iRet = iDao.save" + javaName + "(mapParam);\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new SaveException(e.getMessage(), e);\n" +
                "        }" +
                "\n\t\treturn iRet;" +
                "\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成查询方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录\n"
                + "\t * @param mapParam " + tabelComments + "实体\n"
                + "\t * @return 返回" + tabelComments + "map对象集合,否则返回空集合"
                + "\n\t */\n";
        method = comment + "\tpublic List get" + javaName + "(Map mapParam);\n\n";
        sb.append(method);
        method = comment + "\tpublic List get" + javaName + "(Map mapParam){" +
                "\n\t\tList list = new ArrayList();" +
                "\n\t\ttry {\n" +
                "            list = iDao.get" + javaName + "(mapParam);\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new ServiceLocatorException(e.getMessage(), e);\n" +
                "        }" +
                "\n\t\treturn list;\n\t}\n";
        sbImpl.append(method);

        System.out.println("生成统计方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录数\n"
                + "\t * @param mapParam " + tabelComments + "查询参数\n"
                + "\t * @return 返回" + tabelComments + "记录数"
                + "\n\t */\n";
        method = comment + "\tpublic int get" + javaName + "Count(Map mapParam);\n\n";
        sb.append(method);
        method = comment + "\tpublic int get" + javaName + "Count(Map mapParam){" +
                "\n\t\ttry {\n" +
                "            return iDao.get" + javaName + "Count(mapParam);\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new ServiceLocatorException(e.getMessage(), e);\n" +
                "        }" +
                "\n\t}\n";
        sbImpl.append(method);

        System.out.println("生成删除方法.......");
        comment = "\t/**\n\t * 删除" + tabelComments + "记录\n"
                + "\t * @param mapParam " + tabelComments + "map对象集合\n"
                + "\t * @return 返回操作影响数 0为不成功"
                + "\n\t * @throws DelErrorException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(Map mapParam) throws DelErrorException;";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(Map mapParam) throws DelErrorException{";
        //"\n\t\tiRet = 0;" +

        for (int i = 0; i < priKey.size(); i++) {
            method += "\n\t\tif (JsonHelp.isEmpty(mapParam.get(\"" + priKey.get(i) + "\")))\n" +
                    "            throw new DelErrorException(\"" + mapColcomments.get(priKey.get(i)) + "不允许为空!\");\n";
        }
        method += "\n\t\ttry {\n" +
                //"          for(Iterator it = listMap.iterator(); it.hasNext(); ) {\n" +
                "            return iDao.del" + javaName + "(mapParam);\n" +
                //"          }\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new DelErrorException(e.getMessage(), e);\n" +
                "        }" +
                "\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");

        System.out.println("生成批量删除方法.......");
        comment = "\t/**\n\t * 批量删除" + tabelComments + "记录\n"
                + "\t * @param listMap " + tabelComments + "map对象集合\n"
                + "\t * @return 返回操作影响数 0为不成功"
                + "\n\t * @throws DelErrorException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(List listMap) throws DelErrorException;";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(List listMap) throws DelErrorException{\n\t\tint iRet = 0;\n\t\tMap mapTem;";


        method += "\n\t\ttry {\n" +
                "\t\t\tfor(Iterator it = listMap.iterator(); it.hasNext(); ) {\n"+
                "\t\t\t\tmapTem = (Map)it.next();\n";
        for (int i = 0; i < priKey.size(); i++) {
            method += "\t\t\t\tif (JsonHelp.isEmpty(mapParam.get(\"" + priKey.get(i) + "\")))\n" +
                    "\t\t\t\t\tthrow new DelErrorException(\"" + mapColcomments.get(priKey.get(i)) + "不允许为空!\");\n";
        }
        method += "\n            iRet += iDao.del" + javaName + "(mapParam);\n" +
                "        } catch (DaoException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "            throw new DelErrorException(e.getMessage(), e);\n" +
                "        }\n\t\treturn iRet;" +
                "\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");


        String strFilePath = dbTools.getCODE_DIR() + File.separator + javaName + File.separator;
        File filePath = new File(strFilePath);
        if (!filePath.exists()) {
            filePath.mkdir();
            System.out.println("生成代码目录:" + strFilePath);
        }

        String fileName = strFilePath + boClass + ".java";
        String fileNameImple = strFilePath + boClassImpl + ".java";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            OutputStreamWriter writerImpl = new OutputStreamWriter(
                    new FileOutputStream(fileNameImple), dbTools.getCODE_ENCODE());
            writerImpl.write(sbImpl.toString());
            writerImpl.flush();
            writerImpl.close();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");
    }

    /**
     * 生成Web层的操作类
     *
     * @param javaName 类名
     */
    public void createAction(String javaName) {
        System.out.println("开始生成 " + javaName + " strtus2->Action处理类...");
        String formClass = javaName + "Action";
        String desc1 = "package " + dbTools.getActionPath() + ";\n\n" +
                "import base.service.domain.exception.NoSuchBeanException;\n" +
                "import base.service.domain.tools.BeansHelp;\n" +
                "import base.service.domain.exception.SaveException;\n" +

                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import base.web.actions.BaseAction;\n" +
                "import base.web.tools.WebUtils;\n" +
                "import " + dbTools.getBoPath() + ".IBo" + javaName + ";\n" +
                "/**\n" +
                " * <ol>\n" +
                " * date:" + DbTools.getCurDate() + " editor:" + dbTools.getEDITOR() + "\n" +
                " * <li>创建文档</li>\n" +
                " * <li>" + tabelComments + "数据采集显示及请求流程处理类</li>\n" +
                " * </ol>\n" +
                " * <ol>\n" +
                " *\n";
        if (!"".equals(dbTools.getAUTHOR()))
            desc1 += " * @author <a href=\"" + dbTools.getAUTHOR() + "\">" + dbTools.getEDITOR() + "</a>\n";
        else
            desc1 += " * @author " + dbTools.getEDITOR() + "\n";
        desc1 += " * @version 2.0\n" +
                " * @since 1.6\n" +
                " */\n";
        StringBuffer sb = new StringBuffer(desc1);

        sb.append("public class ").append(formClass).append(" extends BaseAction {\n");
//        sb.append("\tprivate Logger log = Logger.getLogger(this.getClass());\n");
//        sb.append("\tprivate int iFlag = 0;\n");
//        sb.append("\tprivate String msg = \"操作成功\";\n");
//        sb.append("\tprivate List list=new ArrayList();\n");
//        sb.append("\tprivate Map mapParam= new HashMap();\n");
//        sb.append("\tprivate Map mapJsonData= new HashMap();\n");
        sb.append("\tprivate IBo" + javaName + " iBo;\n\n");
        sb.append("\tpublic ").append(formClass).append("(){\n");
        sb.append("\t\ttry {\n" +
                "            iBo = (IBo" + javaName + ") BeansHelp.getBeanInstance(\"bo" + javaName + "\");\n" +
                "        } catch (NoSuchBeanException e) {\n" +
                "            log.error(e.getMessage());\n" +
                "        }")
                .append("\n\t}\n");

        String method, comment;
        comment = "\t/**\n\t * " + tabelComments + "\n"
                + "\t * @return 导航到首页"
                + "\n\t */\n";
        method = comment + "\tpublic String index(){\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 显示" + tabelComments + "添加页面\n"
                + "\t *  "
                + "\n\t */\n";
        method = comment + "\tpublic String viewAdd(){\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 保存" + tabelComments + "记录\n"
                + "\t *  "
                + "\n\t */\n";
        method = comment + "\tpublic String add(){\n\t\t" +
                "try {" +
                "\n\t\t\tMap map = WebUtils.getParameterMap();" +
                "\n\t\t\tcode = iBo.insert" + javaName + "(map);" +
                "\n\t\t\tmsg = \"添加成功\";" +
                "\n\t\t} catch (SaveException e) {" +
                "\n\t\t\te.printStackTrace();" +
                "\n\t\t\tthis.code = -1;" +
                "\n\t\t\tthis.msg = e.getMessage();" +
                "\n\t\t}" +
                "\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 显示" + tabelComments + "修改页面\n"
                + "\t *  "
                + "\n\t */\n";
        method = comment + "\tpublic String viewEdit(){\n\t\t" +
                "try {" +
                "\n\t\t\tMap map = new HashMap();" +
                "\n\t\t\tmap.put(\"ID\", WebUtils.getParamter(\"id\"));" +
                "\n\t\t\tList list = iBo.get" + javaName + "(map);" +
                "\n\t\t\tmodel.put(\"data\", list);" +
                "\n\t\t} catch (Exception e) {" +
                "\n\t\t\te.printStackTrace();" +
                "\n\t\t\tthis.code = -1;" +
                "\n\t\t\tthis.msg = e.getMessage();" +
                "\n\t\t}" +
                "\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 修改" + tabelComments + "记录\n"
                + "\t *  "
                + "\n\t */\n";
        method = comment + "\tpublic String edit(){\n\t\t" +
                "try {" +
                "\n\t\t\tMap map = WebUtils.getParameterMap();" +
                "\n\t\t\tcode = iBo.update" + javaName + "(map);" +
                "\n\t\t\tmsg = \"修改成功\";" +
                "\n\t\t} catch (Exception e) {" +
                "\n\t\t\te.printStackTrace();" +
                "\n\t\t\tthis.code = -1;" +
                "\n\t\t\tthis.msg = e.getMessage();" +
                "\n\t\t}" +
                "\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 删除" + tabelComments + "记录\n"
                + "\t *  "
                + "\n\t */\n";
        method = comment + "\tpublic String delete(){\n\t\t" +
                "try {" +
                "\n\t\t\tMap map = new HashMap();" +
                "\n\t\t\tmap.put(\"ID\", WebUtils.getParamter(\"id\"));" +
                "\n\t\t\tcode = iBo.del" + javaName + "(map);" +
                "\n\t\t\tmsg = \"删除成功\";" +
                "\n\t\t} catch (Exception e) {" +
                "\n\t\t\te.printStackTrace();" +
                "\n\t\t\tthis.code = -1;" +
                "\n\t\t\tthis.msg = e.getMessage();" +
                "\n\t\t}" +
                "\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);

        comment = "\t/**\n\t * 显示" + tabelComments + "列表\n"
                + "\t *  "
                + "\n\t */\n";
        method = comment + "\tpublic String viewList(){\n\t\t" +
                "try {" +
                "\n\t\t\tMap map = WebUtils.getParameterMap();" +
                "\n\t\t\tList list = iBo.get" + javaName + "(map);" +
                "\n\t\t\tmodel.put(\"data\",list);" +
                "\n\t\t} catch (Exception e) {" +
                "\n\t\t\te.printStackTrace();" +
                "\n\t\t\tthis.code = -1;" +
                "\n\t\t\tthis.msg = e.getMessage();" +
                "\n\t\t}" +
                "\n\t\treturn getResult();\n\t}\n\n";
        sb.append(method);


        sb.append("\n}\n");

        String strFilePath = dbTools.getCODE_DIR() + File.separator + javaName + File.separator;
        File filePath = new File(strFilePath);
        if (!filePath.exists()) {
            filePath.mkdir();
            System.out.println("生成代码目录:" + dbTools.getCODE_DIR());
        }

        String fileName = strFilePath + formClass + ".java";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            writer.write(sb.toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");
    }

    /**
     * 生成spring struts配置文件
     *
     * @param javaName
     */
    public void createConfig(String javaName) {
        System.out.println("开始生成 " + javaName + " 配置文件...");
        //fillDllArrayList(javaName);
        StringBuffer sb = new StringBuffer("<!--");
        sb.append(tabelComments).append("spring 配置-->\n");
        //dao
        sb.append("<bean id=\"").append("dao").append(javaName).append("\" class=\"").append(dbTools.getDaoPath() + ".impl.Dao" + javaName + "Impl").append("\">\n");
        sb.append("\t<constructor-arg index=\"0\" ref=\"").append(dbTools.getSqlMapClientName()).append("\"/>\n");
        sb.append("</bean>\n\n");
        //bo
        sb.append("<bean id=\"").append("bo").append(javaName).append("\" parent=\"").append(dbTools.getTxTransactionProxyName()).append("\">\n");
        sb.append("\t<property name=\"target\">\n");
        sb.append("\t\t<bean class=\"").append(dbTools.getBoPath()).append(".impl.Bo" + javaName + "Impl").append("\">\n");
        sb.append("\t\t\t<constructor-arg index=\"0\" ref=\"").append("dao" + javaName).append("\"/>\n");
        sb.append("\t\t</bean>\n");
        sb.append("\t</property>\n");
        sb.append("</bean>\n\n");

        sb.append("<!--" + tabelComments).append("struts 配置-->\n");
        sb.append("<action name=\"index\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"index\">\n");
        sb.append("\t<result name=\"success\" type=\"dispatcher\">主页面路径</result>");
        sb.append("\n</action>\n");

        sb.append("<action name=\"viewAdd\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"viewAdd\">\n");
        sb.append("\t<result name=\"success\" type=\"dispatcher\">添加页面路径</result>");
        sb.append("\n</action>\n");

        sb.append("<action name=\"add\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"add\">\n");
        sb.append("\t<result name=\"success\" type=\"jsonResult\"></result>");
        sb.append("\n</action>\n");

        sb.append("<action name=\"viewEdit\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"viewEdit\">\n");
        sb.append("\t<result name=\"success\" type=\"dispatcher\">修改页面路径</result>");
        sb.append("\n</action>\n");

        sb.append("<action name=\"edit\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"edit\">\n");
        sb.append("\t<result name=\"success\" type=\"jsonResult\"></result>");
        sb.append("\n</action>\n");

        sb.append("<action name=\"delete\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"delete\">\n");
        sb.append("\t<result name=\"success\" type=\"jsonResult\"></result>");
        sb.append("\n</action>\n");

        sb.append("<action name=\"viewList\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"viewList\">\n");
        sb.append("\t<result name=\"success\" type=\"jsonHtml\">数据列表页面</result>\n");
        sb.append("\t<result name=\"jsonResult\" type=\"jsonResult\"></result>");
        sb.append("\n</action>\n");

        /**
         sb.append("<action name=\"insert").append(javaName).append("\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"").append("insert" + javaName).append("\"/>\n");
         sb.append("<action name=\"update").append(javaName).append("\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"").append("update" + javaName).append("\"/>\n");
         sb.append("<action name=\"update").append(javaName).append("Dynamic\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"").append("update" + javaName).append("Dynamic\"/>\n");
         sb.append("<action name=\"del").append(javaName).append("\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"").append("del" + javaName).append("\"/>\n");
         sb.append("<action name=\"get").append(javaName).append("\" class=\"").append(dbTools.getActionPath() + "." + javaName + "Action").append("\" method=\"").append("get" + javaName).append("\"/>\n");
         */

        String strFilePath = dbTools.getCODE_DIR() + File.separator + javaName + File.separator;
        File filePath = new File(strFilePath);
        if (!filePath.exists()) {
            filePath.mkdir();
            System.out.println("生成代码目录:" + dbTools.getCODE_DIR());
        }
        String fileName = strFilePath + javaName + "-conf.xml";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");

    }

    public void createPageByTemplate() {

    }

    public static void main(String args[]) {
        SqlMap_Oracle sqlMap = new SqlMap_Oracle();
        if (args.length == 0)
            System.out.println("参数不允许为空");
        else {
            for (int i = 0; i < args.length; i++) {
                sqlMap.fillDllArrayList(args[i].toUpperCase());
//                sqlMap.createPojo(args[i].toUpperCase());
                sqlMap.createSqlMapXml(args[i].toUpperCase(), args[i].toUpperCase());
                sqlMap.createDao(args[i].toUpperCase(), args[i].toUpperCase());
                sqlMap.createBo(args[i].toUpperCase());
                sqlMap.createAction(args[i].toUpperCase());
                sqlMap.createConfig(args[i].toUpperCase());
            }
        }
        dbTools.closeDb();
    }
}

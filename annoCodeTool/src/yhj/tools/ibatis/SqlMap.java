package yhj.tools.ibatis;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
 * Date: 2005-8-26  editor:yanghongjian
 * <li>生成POJO、SQLMAP文档
 * </ol>
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 * @since 1.4.1
 */
public class SqlMap {
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

    public SqlMap() {
    }

    public static DbTools dbTools = DbTools.getInstance();
    private ArrayList al;
    private String tabelComments;
    private Map mapColcomments;

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
        if (str.length() > 0) {
            try {
                str = new String(str.getBytes("ISO8859_1"), "GBK");
            } catch (Exception e) {
                str = str;
            }
        }
        return str;
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

    private void fillDllArrayList(String tableName) {
        if (al == null)
            al = new ArrayList();
        else
            al.clear();

        getTableColComments(tableName);

        String sql = "select * from " + tableName + " t where rownum = 1";
        ResultSet rs = dbTools.getResultSet(sql);
        try {
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
        sb.append("\t\t\t\tinsert into ").append(sqlName).append("(");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size())) {
                sb.append(ddl.fieldName).append(",\n\t\t\t\t");
            } else {
                sb.append(ddl.fieldName).append(',');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")\n" +
                "\t\t\t\tvalues(");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,\n\t\t\t\t");
            else
                sb.append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");\n");
        System.out.println("生成添加(INSERT)SQL......");
        return sb;
    }

    private StringBuffer createSqlMapXmlUpdate(String sqlName) {
        StringBuffer sb = new StringBuffer();
        sb.append("\t\t\t\tupdate ").append(sqlName).append("\n\t\t\t\tset ");
        for (int i = 0; i < al.size(); i++) {
            DDL ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,\n\t\t\t\t");
            else
                sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n\t\t\t\twhere ;");
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
        sb.append("\t<delete id=\"del").append(sqlName).append("\" parameterClass=\"").append(sqlName.toLowerCase()).append("\">\n");
        sb.append("\t\tdelete from ").append(sqlName);
        sb.append(" where \n");
        sb.append("\t</delete>\n");
        System.out.println("开始生成删除(DELETE)SQL...");
        return sb;
    }

    /**
     * 生成ibatis的sqlMap文档
     *
     * @param sqlName sqlMap文档的名称
     */
    public void createSqlMapXml(String sqlName) {
        System.out.println("开始生成sqlMap文档...");
        StringBuffer sb = new StringBuffer("");
        String str = "<?xml version=\"1.0\" encoding=\"gb2312\"?>\n" +
                "<!DOCTYPE sqlMap\n" +
                "    PUBLIC \"-//ibatis.apache.org//DTD SQL Map 2.0//EN\"\n" +
                "    \"http://ibatis.apache.org/dtd/sql-map-2.dtd\">\n";
        sb.append(str);
        sb.append("\t<sqlMap namespace=\"").append(sqlName).append("\">\n");
        sb.append("\t<typeAlias alias=\"").append(sqlName.toLowerCase()).append("\" type=\"").append(sqlName).append("\"/>\n");
        sb.append("    <cacheModel id=\"cache" + sqlName + "\" type=\"LRU\">\n" +
                "        <flushInterval hours=\"24\"/>\n" +
                "        <flushOnExecute statement=\"save" + sqlName + "\"/>\n" +
                "        <flushOnExecute statement=\"del" + sqlName + "\"/>\n" +
                "        <property name=\"size\" value=\"100\"/>\n" +
                "    </cacheModel>\n");

        System.out.println("生成resultMap...");
        sb.append("\t<resultMap id=\"result").append(sqlName).append("\" class=\"").append(sqlName.toLowerCase()).append("\">\n");
        DDL ddl;
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
        System.out.println("生成查找SQL...");
        sb.append("\t<select id=\"get").append(sqlName).append("\" parameterClass=\"").append(sqlName.toLowerCase()).append("\" resultMap=\"result").append(sqlName.toUpperCase()).append("\" cacheModel=\"cache").append(sqlName.toUpperCase()).append("\">\n");
        sb.append("\t\tselect ");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append(ddl.fieldName).append(",\n\t\t\t");
            else
                sb.append(ddl.fieldName).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        sb.append("\t\tfrom ").append(sqlName).append('\n');
        sb.append("\t</select>\n");

        System.out.println("生成添加SQL....");
        sb.append("\t<insert id=\"save" + sqlName + "\" parameterClass=\"" + sqlName.toLowerCase() + "\">\n" +
//                " \t\t<![CDATA[\n" +
                "\t\tDECLARE\n" +
                "\t\t\tn_count number(1);\n" +
                "\t\tBEGIN\n" +
                "\t\t\tselect count(1) into n_count from " + sqlName + "\n" +
                "\t\t\twhere \n")
                .append("\t\t\tIF n_count=0 THEN\n")
                .append(createSqlMapXmlInsert(sqlName))//添加记录
                .append("\t\t\tELSE\n")
                .append(createSqlMapXmlUpdate(sqlName))//更新记录
                .append("\n\t\t\tEND IF;\n\t\tEND;" +
//                        "\n\t\t]]>" +
                        "\n\t</insert>\n");
        sb.append(createSqlMapXmlDel(sqlName));
        sb.append("</sqlMap>\n");
        String fileName = "c:\\" + sqlName + ".xml";
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
     * 生成持久化实体的操作类
     *
     * @param javaName 类名
     */
    public void createDao(String javaName) {
        System.out.println("开始生成 " + javaName + " Dao数据操作类...");
        String daoClass = "IDao" + javaName;
        String daoClassImpl = "Dao" + javaName + "Impl";
        StringBuffer sb = new StringBuffer();
        StringBuffer sbImpl = new StringBuffer();
        sb.append("//").append(tabelComments).append("数据操作接口\n\n");
        sbImpl.append("//").append(tabelComments).append("数据操作接口实现类\n\n");

        sb.append("public class ").append(daoClass).append(" extends IDao {\n");
        sbImpl.append("public class ").append(daoClassImpl).append(" extends BaseDao implements ").append(daoClass).append(" {\n");
        sbImpl.append("\tpublic ").append(daoClassImpl).append("(){\n\t\tlog = Logger.getLogger(this.getClass());\n\t}\n\n");

        String method, comment;
        System.out.println("生成保存方法.......");
        comment = "\t/**\n\t * 保存" + tabelComments + "记录\n"
                + "\t * @param obj " + tabelComments + "实体\n"
                + "\t * @return 返回操作影响数 1为成功 0为不成功"
                + "\n\t */\n";
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj);\n\n";
        sb.append(method);
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj){\n\t\treturn 1;\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成查询方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录\n"
                + "\t * @param obj " + tabelComments + "实体\n"
                + "\t * @return 返回" + tabelComments + "实体,否则返回null"
                + "\n\t */\n";
        method = comment + "\tpublic " + javaName + " get" + javaName + "(" + javaName + " obj);\n\n";
        sb.append(method);
        method = comment + "\tpublic " + javaName + " get" + javaName + "(" + javaName + " obj){\n\t\t return null;\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成删除方法.......");
        comment = "\t/**\n\t * 删除" + tabelComments + "记录\n"
                + "\t * @param obj " + tabelComments + "实体\n"
                + "\t * @return 返回操作影响数 1为成功 0为不成功"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(" + javaName + " obj);";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(" + javaName + " obj){\n\t\t return 0;\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");
        String fileName = "c:\\" + daoClass + ".java";
        String fileNameImple = "c:\\" + daoClassImpl + ".java";
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(sb.toString());
            writer.close();
            FileWriter writerImpl = new FileWriter(fileNameImple);
            writerImpl.write(sbImpl.toString());
            writerImpl.close();
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
    public void createBo(String javaName) {
        System.out.println("开始生成 " + javaName + " Bo业务处理类...");
        String boClass = "IBo" + javaName;
        String boClassImpl = "Bo" + javaName + "Impl";
        StringBuffer sb = new StringBuffer();
        StringBuffer sbImpl = new StringBuffer();
        sb.append("//").append(tabelComments).append("业务处理接口\n\n");
        sbImpl.append("//").append(tabelComments).append("业务处理接口实现类\n\n");

        sb.append("public class ").append(boClass).append(" extends IBo {\n");
        sbImpl.append("public class ").append(boClassImpl).append(" implements ").append(boClass).append(" {\n");
        sbImpl.append("\tpublic ").append(boClassImpl).append("(){\n\t\t\n\t}\n\n");

        String method, comment;
        System.out.println("生成保存方法.......");
        comment = "\t/**\n\t * 保存" + tabelComments + "记录\n"
                + "\t * @param obj " + tabelComments + "实体\n"
                + "\t * @return 返回操作影响数 1为成功 0为不成功"
                + "\n\t */\n";
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj);\n\n";
        sb.append(method);
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj){\n\t\treturn 1;\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成查询方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录\n"
                + "\t * @param obj " + tabelComments + "实体\n"
                + "\t * @return 返回" + tabelComments + "实体,否则返回null"
                + "\n\t */\n";
        method = comment + "\tpublic " + javaName + " get" + javaName + "(" + javaName + " obj);\n\n";
        sb.append(method);
        method = comment + "\tpublic " + javaName + " get" + javaName + "(" + javaName + " obj){\n\t\t return null;\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成删除方法.......");
        comment = "\t/**\n\t * 删除" + tabelComments + "记录\n"
                + "\t * @param obj " + tabelComments + "实体\n"
                + "\t * @return 返回操作影响数 1为成功 0为不成功"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(" + javaName + " obj);";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(" + javaName + " obj){\n\t\t return 0;\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");
        String fileName = "c:\\" + boClass + ".java";
        String fileNameImple = "c:\\" + boClassImpl + ".java";
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(sb.toString());
            writer.close();
            FileWriter writerImpl = new FileWriter(fileNameImple);
            writerImpl.write(sbImpl.toString());
            writerImpl.close();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }
        System.out.println("生成成功");
    }

    public static void main(String args[]) {
        SqlMap sqlMap = new SqlMap();
        if (args.length == 0)
            System.out.println("参数不允许为空。");
        else {
            for (int i = 0; i < args.length; i++) {
                sqlMap.createPojo(args[i].toUpperCase());
                sqlMap.createSqlMapXml(args[i].toUpperCase());
                sqlMap.createDao(args[i].toUpperCase());
                sqlMap.createBo(args[i].toUpperCase());
            }
        }
        dbTools.closeDb();
    }
}

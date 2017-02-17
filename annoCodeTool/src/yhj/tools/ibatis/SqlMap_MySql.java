package yhj.tools.ibatis;

import com.mysql.jdbc.DatabaseMetaData;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * Date: 2005-8-26  editor:yanghongjian
 * <li>生成POJO、SQLMAP文档
 * </ol>
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 * @since 1.4.1
 */
public class SqlMap_MySql {
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
        /**
         * 默认值
         */
        String fieldDefault;
    }

    public SqlMap_MySql() {
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
        String str1 = "";
        if (str == null || str.length() == 0)
            str = "";
        str = str.trim();
        if (str.length() > 0) {
            try {
                str1 = new String(str.getBytes("ISO8859_1"), "GBK");
            } catch (Exception e) {
                str1 = str;
            }
        }
        return str1;
    }

    /**
     * 获取表、列的注释
     *
     * @param tableName 表名
     */
    private void getTableColComments(String tableName) {

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
        String sql = "SHOW TABLE STATUS FROM " + dbTools.getDBMS_DB();
        ResultSet rs = dbTools.getResultSet(sql);
        try {
            while (rs.next()) {
                if (rs.getString(1).toUpperCase().equals(tableName)) {
                    tabelComments = rs.getString(18) != null ? rs.getString(18) : "";
                    break;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (al == null)
            al = new ArrayList();
        else
            al.clear();

        sql = "SHOW FULL COLUMNS FROM " + tableName;
        rs = dbTools.getResultSet(sql);
        try {
            while (rs.next()) {
                DDL ddl = new DDL();
                //获取指定列的名称
                ddl.fieldName = rs.getString(1).toUpperCase();
                ddl.javaAtrriber = ddl.fieldName;
                //检索列的特定于数据库的类型名
                ddl.fieldSqlType = rs.getString(2).toUpperCase();
                ddl.fieldDefault = rs.getString(6) != null ? rs.getString(6) : "";
                ddl.fieldComments = rs.getString(9) != null ? rs.getString(9) : "";

                System.out.println("name=" + ddl.fieldName
                        + "\tsqltype=" + ddl.fieldSqlType
                        + "\t comment=" + ddl.fieldComments);
                ddl.javaType = "String";
                ddl.sqlMapType = ddl.fieldSqlType;
                ddl.sqlMapJavaType = "string";

                al.add(ddl);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void fillDllArrayList_(String tableName) {
        if (al == null)
            al = new ArrayList();
        else
            al.clear();

        getTableColComments(tableName);
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        try {
            metaData = (DatabaseMetaData) dbTools.getConn().getMetaData();
            rs = metaData.getColumns(null, "%", tableName, "%");
            while (rs.next()) {
                DDL ddl = new DDL();
                //获取指定列的名称
                ddl.fieldName = rs.getString("COLUMN_NAME");
                ddl.javaAtrriber = ddl.fieldName;
                //检索列的特定于数据库的类型名
                ddl.fieldSqlType = rs.getString("TYPE_NAME");
                ddl.fieldDefault = rs.getString("COLUMN_DEF") != null ? rs.getString("COLUMN_DEF") : "";
                ddl.fieldComments = getFieldComments(ddl.fieldName);

                System.out.println("name=" + ddl.fieldName
                        + "\tsqltype=" + ddl.fieldSqlType
                        + "\t comment=" + ddl.fieldComments);
                ddl.javaType = "String";
                ddl.sqlMapType = ddl.fieldSqlType;
                ddl.sqlMapJavaType = "string";
                /*
                if (ddl.fieldSqlType.equals("CHAR")
                        || ddl.fieldSqlType.equals("VARCHAR")
                        || ddl.fieldSqlType.equals("TINYTEXT")
                        || ddl.fieldSqlType.equals("TEXT")
                        || ddl.fieldSqlType.equals("MEDIUMTEXT")
                        || ddl.fieldSqlType.equals("LONGTEXT")) {//VARCHAR
                    ddl.javaType = "String";
                    ddl.sqlMapType = "VARCHAR";
                    ddl.sqlMapJavaType = "string";
                }
                if (ddl.fieldSqlType.equals("DATE")
                        ||ddl.fieldSqlType.equals("YEAR")) {//DATE
                    ddl.javaType = "Date";
                    ddl.sqlMapType = "DATE";
                    ddl.sqlMapJavaType = "date";
                }
                if (ddl.fieldSqlType.equals("DATETIME")
                        ||ddl.fieldSqlType.equals("TIMESTAMP")) {//DATE
                    ddl.javaType = "Date";
                    ddl.sqlMapType = "DATETIME";
                    ddl.sqlMapJavaType = "date";
                }
                if (ddl.fieldSqlType.equals("SMALLINT")
                        || ddl.fieldSqlType.equals("INT")
                        || ddl.fieldSqlType.equals("TINYINT")
                        || ddl.fieldSqlType.equals("MEDIUMINT")
                        || ddl.fieldSqlType.equals("BIGINT")
                        || ddl.fieldSqlType.equals("FLOAT")
                        || ddl.fieldSqlType.equals("DOUBLE")
                        || ddl.fieldSqlType.equals("DECIMAL")) {//NUMERIC
                    ddl.javaType = "String";
                    ddl.sqlMapType = "SMALLINT";
                    ddl.sqlMapJavaType = "string";
                }
                */
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
//            if (!ddl.javaType.equals("Date"))
            sb.append("=\"").append(ddl.fieldDefault).append("\";\n");
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
        System.out.println(dbTools.getCODE_DIR());
        if (!(new File(dbTools.getCODE_DIR()).isDirectory())) {
            new File(dbTools.getCODE_DIR()).delete();
            new File(dbTools.getCODE_DIR()).mkdir();
        }
        String fileName = dbTools.getCODE_DIR() + javaName + ".java";
        try {
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
     * 生成添加sql
     *
     * @param sqlName 表名
     * @return sql字符串
     */
    private StringBuffer createSqlMapXmlInsert(String sqlName) {
        DDL ddl;
        StringBuffer sb = new StringBuffer();
        sb.append("\t<insert id=\"insert").append(sqlName).append("\" parameterClass=\"").append(sqlName.toLowerCase()).append("\">\n");
        sb.append("\t\tINSERT INTO ").append(sqlName).append("(");
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
                "\t\tVALUES(");
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,\n\t\t\t\t");
            else
                sb.append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")\n");
        sb.append("\t</insert>\n");
        System.out.println("生成添加(INSERT)SQL......");
        return sb;
    }

    private StringBuffer createSqlMapXmlUpdate(String sqlName) {
        StringBuffer sb = new StringBuffer();
        sb.append("\t<update id=\"update").append(sqlName).append("\" parameterClass=\"").append(sqlName.toLowerCase()).append("\">\n");
        sb.append("\t\tUPDATE ").append(sqlName).append("\n\t\t\t\tset ");
        for (int i = 0; i < al.size(); i++) {
            DDL ddl = (DDL) al.get(i);
            int rel = i + 1;
            if ((rel % 5 == 0) && (rel != al.size()))
                sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,\n\t\t\t\t");
            else
                sb.append(ddl.fieldName).append('=').append('#').append(ddl.javaAtrriber).append(":").append(ddl.sqlMapType).append("#,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n\t\tWHERE \n");
        sb.append("\t</update>\n");
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
        sb.append("\t<delete id=\"del").append(sqlName).append("\" parameterClass=\"string\">\n");
        sb.append("\t\tDELETE from ").append(sqlName);
        sb.append(" WHERE \n");
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
        String str = "<?xml version=\"1.0\" encoding=\"" + dbTools.getCODE_ENCODE() + "\"?>\n<!DOCTYPE sqlMap PUBLIC \"-//ibatis.apache.org//DTD SQL Map 2.0//EN\"\n" +
                "   \"http://ibatis.apache.org/dtd/sql-map-2.dtd\">\n";
        sb.append(str);
        sb.append("<sqlMap namespace=\"").append(sqlName).append("\">\n");
        sb.append("<typeAlias alias=\"").append(sqlName.toLowerCase()).append("\" type=\"").append(sqlName).append("\"/>\n");
        sb.append("    <cacheModel id=\"cache").append(sqlName).append("\" type=\"LRU\">\n" + "        <flushInterval hours=\"24\"/>\n" + "        <flushOnExecute statement=\"insert").append(sqlName).append("\"/>\n        <flushOnExecute statement=\"update").append(sqlName).append("\"/>\n        <flushOnExecute statement=\"del").append(sqlName).append("\"/>\n        <property name=\"size\" value=\"100\"/>\n    </cacheModel>\n");

        System.out.println("生成resultMap...");
        sb.append("\t<resultMap id=\"result").append(sqlName).append("\" class=\"").append(sqlName.toLowerCase()).append("\">\n");
        DDL ddl;
        for (int i = 0; i < al.size(); i++) {
            ddl = (DDL) al.get(i);
            sb.append("\t\t<result property=\"").append(ddl.javaAtrriber).append("\" javaType=\"")
                    .append(ddl.sqlMapJavaType).append("\" column=\"").append(ddl.fieldName).append("\" jdbcType=\"").append(ddl.sqlMapType)
                    .append("\" nullValue=\"").append(ddl.fieldDefault).append("\"/>\n");
        }
        sb.append("\t</resultMap>\n");
        System.out.println("生成查找SQL...");
        sb.append("\t<select id=\"get").append(sqlName).append("\" parameterClass=\"").append(sqlName.toLowerCase()).append("\" resultMap=\"result").append(sqlName.toUpperCase()).append("\" cacheModel=\"cache").append(sqlName.toUpperCase()).append("\">\n");
        sb.append("\t\tSELECT ");
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
        sb.append("\t\tFROM ").append(sqlName);
        sb.append("\n\t</select>\n");

        System.out.println("生成添加SQL....");
        sb.append(createSqlMapXmlInsert(sqlName));
        sb.append(createSqlMapXmlUpdate(sqlName));
        sb.append(createSqlMapXmlDel(sqlName));
        sb.append("</sqlMap>\n");

        String fileName = dbTools.getCODE_DIR() + sqlName + ".xml";
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
                + "\t * @param obj " + tabelComments + "对象\n"
                + "\t * @return 返回操作影响数 大于1为成功 0为不成功\n"
                + "\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj)throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj)throws DaoException {" +
                "\n       try {\n" +
                "            sqlMap.startTransaction();\n" +
                "            sqlMap.startBatch();\n" +
                "            Map map = new HashMap();\n" +
                "            map.put(\"XXXX_DM\", obj.getXXXX_DM());\n" +
                "            if (get" + javaName + "(map).size() > 0)\n" +
                "               sqlMap.update(\"update" + javaName + "\", obj);\n" +
                "            else\n" +
                "               sqlMap.insert(\"insert" + javaName + "\", obj);\n" +
                "            sqlMap.executeBatch();\n" +
                "            sqlMap.commitTransaction();\n" +
                "        } catch (SQLException e) {\n" +
                "            String err = \"成批添加或更新出错->出错原因:\" + e.getMessage();\n" +
                "            log.error(err, e);\n" +
                "            throw new DaoException(err, e);\n" +
                "        } finally {\n" +
                "            try {\n" +
                "                sqlMap.endTransaction();\n" +
                "            } catch (SQLException e) {\n" +
                "                log.error(e.getMessage());\n" +
                "            }\n" +
                "        }\n" +
                "        return 1;\n"
                + "}\n\n";
        sbImpl.append(method);

        System.out.println("生成查询方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录\n"
                + "\t * @param mapParam 查询参数\n"
                + "\t * @return 返回" + tabelComments + "实体对象集合\n"
                + "\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic List get" + javaName + "(Map mapParam)throws DaoException;\n\n";
        sb.append(method);
        method = comment + "\tpublic List get" + javaName + "(Map mapParam)throws DaoException{" +
                "\n        List listMap = new ArrayList();\n" +
                "        try {\n" +
                "            listMap = sqlMap.queryForList(\"get" + javaName + "\", mapParam);\n" +
                "        } catch (Exception e) {\n" +
                "            String err = \"查询出错->出错原因:\" + e.getMessage();\n" +
                "            log.error(err, e);\n" +
                "            throw new DaoException(err, e);\n" +
                "        }\n" +
                "        return listMap;"
                + "\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成删除方法.......");
        comment = "\t/**\n\t * 根据主键成批删除" + tabelComments + "记录\n"
                + "\t * @param listDm 主键的集合\n"
                + "\t * @return 返回操作影响数\n"
                + "\t * @throws DaoException 出错"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(List listDm)throws DaoException;";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(List listDm)throws DaoException{\n" +
                "        try {\n" +
                "            sqlMap.startTransaction();\n" +
                "            sqlMap.startBatch();\n" +
                "            for (Iterator it = listDm.iterator(); it.hasNext();)\n" +
                "                sqlMap.update(\"del" + javaName + "\", it.next());\n" +
                "\n" +
                "            sqlMap.executeBatch();\n" +
                "            sqlMap.commitTransaction();\n" +
                "        } catch (SQLException e) {\n" +
                "            String err = \"成批删除-->出错原因:\" + e.getMessage();\n" +
                "            log.error(err, e);\n" +
                "            throw new DaoException(err, e);\n" +
                "        } finally {\n" +
                "            try {\n" +
                "                sqlMap.endTransaction();\n" +
                "            } catch (SQLException e) {\n" +
                "                log.error(e.getMessage());\n" +
                "            }\n" +
                "        }\n" +
                "        return listDm.size();"
                + "\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");

        String fileName = dbTools.getCODE_DIR() + daoClass + ".java";
        String fileNameImpl = dbTools.getCODE_DIR() + daoClassImpl + ".java";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            out.write(sb.toString());
            out.flush();
            out.close();
            OutputStreamWriter outImpl = new OutputStreamWriter(
                    new FileOutputStream(fileNameImpl), dbTools.getCODE_ENCODE());
            outImpl.write(sbImpl.toString());
            outImpl.flush();
            outImpl.close();
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
                + "\t * @param obj " + tabelComments + "对象\n"
                + "\t * @return 返回操作影响数 大于1为成功 0为不成功\n"
                + "\t * @throws SaveException 保存时出错"
                + "\n\t */\n";
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj) throws SaveException ;\n\n";
        sb.append(method);
        method = comment + "\tpublic int save" + javaName + "(" + javaName + " obj) throws SaveException {\n" +
                "        int i;\n" +
                "        try {\n" +
                "            i = iDao.save" + javaName + "(obj);\n" +
                "        } catch (DaoException e) {\n" +
                "            throw new SaveException(e.getMessage(), e);\n" +
                "        }\n" +
                "        return i;"
                + "\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成查询方法.......");
        comment = "\t/**\n\t * 查询" + tabelComments + "记录\n"
                + "\t * @param mapParam 查询参数\n"
                + "\t * @return 返回" + tabelComments + "实体对象集合"
                + "\n\t */\n";
        method = comment + "\tpublic List get" + javaName + "(Map mapParam);\n\n";
        sb.append(method);
        method = comment + "\tpublic List get" + javaName + "(Map mapParam){\n" +
                "        List list;\n" +
                "        try {\n" +
                "            list = iDao.get" + javaName + "(mapParam);\n" +
                "        } catch (DaoException e) {\n" +
                "            throw new ServiceLocatorException(e.getMessage(), e);\n" +
                "        }\n" +
                "        return list;"
                + "\n\t}\n\n";
        sbImpl.append(method);

        System.out.println("生成删除方法.......");
        System.out.println("生成删除方法.......");
        comment = "\t/**\n\t * 根据主键成批删除" + tabelComments + "记录\n"
                + "\t * @param listDm 主键的集合\n"
                + "\t * @return 返回操作影响数\n"
                + "\t * @throws DelErrorException 删除时出错"
                + "\n\t */\n";
        method = comment + "\tpublic int del" + javaName + "(List listDm)throws DelErrorException;";
        sb.append(method);
        method = comment + "\tpublic int del" + javaName + "(List listDm)throws DelErrorException{\n" +
                "        int i;\n" +
                "        try {\n" +
                "            i = iDao.del" + javaName + "(listDm);\n" +
                "        } catch (DaoException e) {\n" +
                "            throw new DelErrorException(e.getMessage(), e);\n" +
                "        }\n" +
                "        return i;"
                + "\n\t}\n";
        sbImpl.append(method);

        sb.append("\n}\n");
        sbImpl.append("}\n");

        String fileName = dbTools.getCODE_DIR() + boClass + ".java";
        String fileNameImpl = dbTools.getCODE_DIR() + boClassImpl + ".java";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            out.write(sb.toString());
            out.flush();
            out.close();
            OutputStreamWriter outImpl = new OutputStreamWriter(
                    new FileOutputStream(fileNameImpl), dbTools.getCODE_ENCODE());
            outImpl.write(sbImpl.toString());
            outImpl.flush();
            outImpl.close();
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
    public void createJs(String javaName) {
        System.out.println("开始生成 " + javaName + " JS业务片段功能代码...");
        String boClass = "IBo" + javaName;
        String boClassImpl = "Bo" + javaName + "Impl";
        StringBuffer sb = new StringBuffer();
        StringBuffer sbImpl = new StringBuffer();
        sb.append("//").append(tabelComments).append("业务处理接口\n\n");
        sbImpl.append("//").append(tabelComments).append("业务处理接口实现类\n\n");
        String fileName = dbTools.getCODE_DIR() + boClass + ".java";
        String fileNameImpl = dbTools.getCODE_DIR() + boClassImpl + ".java";
        try {
            System.out.println("code_encode=" + dbTools.getCODE_ENCODE());
            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileName), dbTools.getCODE_ENCODE());
            out.write(sb.toString());
            out.flush();
            out.close();
            OutputStreamWriter outImpl = new OutputStreamWriter(
                    new FileOutputStream(fileNameImpl), dbTools.getCODE_ENCODE());
            outImpl.write(sbImpl.toString());
            outImpl.flush();
            outImpl.close();
        } catch (FileNotFoundException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("生成失败");
            e.printStackTrace();
        }

        System.out.println("生成成功");
    }

    public static void main(String args[]) {
        SqlMap_MySql sqlMap = new SqlMap_MySql();
        if (args.length == 0)
            System.out.println("参数不允许为空.");
        else {
            for (int i = 0; i < args.length; i++) {
                sqlMap.fillDllArrayList(args[i].toUpperCase());
                System.out.println("表名:" + args[i] + "\t");
//                sqlMap.createPojo(args[i].toUpperCase());
                sqlMap.createSqlMapXml(args[i].toUpperCase());
//                sqlMap.createDao(args[i].toUpperCase());
//                sqlMap.createBo(args[i].toUpperCase());
            }
        }
        dbTools.closeDb();
    }
}
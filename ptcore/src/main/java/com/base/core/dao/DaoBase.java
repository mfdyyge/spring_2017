package com.base.core.dao;

import com.base.core.domain.exception.DaoException;
import com.base.core.domain.tools.BaseTools;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * Date: 2014-08-26<br>
 * <li>创建文档</li>
 * <li>Dao层核心类，提供通用数据库操作
 * <pre>
 *     使用说明：
 *     1.在spring中注入sqlmap文件对应的命名空间；
 *     2.业务类数据操作类可以继承，自动获取通用Dao方法，无需再编写代码；
 *     3.业务类数据操作类在继承时需要修改SQLID_xxx的标识、或者使用属性注入
 *     4.需要更底层的数据库操作时，业务系统需要加载BASE.xml的sqlmap
 *     5.使用ibatis的iterate执行批量SQL时请使用execStatement函数
 * </pre>
 * </li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
@Repository("daoBase")
public class DaoBase extends SqlMapClientDaoSupport implements IDao {
    private Logger logger = Logger.getLogger(this.getClass());
    // sqlmap.xml定义文件中对应的sqlid
    protected String SQLID_INSERT = "insert";
    protected String SQLID_SAVE = "save";
    protected String SQLID_UPDATE = "update";
    protected String SQLID_DELETE = "delete";
    protected String SQLID_SELECT = "select";
    @Resource(name="sqlMapClientYpt")  //通过bean名称注入  
    private SqlMapClient sqlMapClient;  
   
    @PostConstruct  //完成sqlMapClient初始化工作   
    public void initSqlMapClient(){  
        super.setSqlMapClient(sqlMapClient);  
    }  
    
    public DaoBase() {
    }

    private String sqlmapNamespace = "";

    /**
     * sqlmapNamespace，对应sqlmap.xml中的命名空间
     *
     * @return 返回指定的sqlmap的命名空间
     */
    public String getSqlmapNamespace() {
        return sqlmapNamespace;
    }

    /**
     * sqlmapNamespace的设置方法，可以用于spring注入
     *
     * @param sqlmapNamespace 命名空间
     */
    public void setSqlmapNamespace(String sqlmapNamespace) {
        this.sqlmapNamespace = sqlmapNamespace.length() > 0
                ? sqlmapNamespace + "." : "";
    }

    public String getSQLID_INSERT() {
        return SQLID_INSERT;
    }

    public void setSQLID_INSERT(String SQLID_INSERT) {
        this.SQLID_INSERT = SQLID_INSERT;
    }

    public String getSQLID_SAVE() {
        return SQLID_SAVE;
    }

    public void setSQLID_SAVE(String SQLID_SAVE) {
        this.SQLID_SAVE = SQLID_SAVE;
    }

    public String getSQLID_UPDATE() {
        return SQLID_UPDATE;
    }

    public void setSQLID_UPDATE(String SQLID_UPDATE) {
        this.SQLID_UPDATE = SQLID_UPDATE;
    }

    public String getSQLID_DELETE() {
        return SQLID_DELETE;
    }

    public void setSQLID_DELETE(String SQLID_DELETE) {
        this.SQLID_DELETE = SQLID_DELETE;
    }

    public String getSQLID_SELECT() {
        return SQLID_SELECT;
    }

    public void setSQLID_SELECT(String SQLID_SELECT) {
        this.SQLID_SELECT = SQLID_SELECT;
    }

    /**
     * 返回当前数据库连接对象
     *
     * @return 连接对象
     */
    @Override
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = getSqlMapClient().getDataSource().getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return conn;
    }

    /**
     * 获取数据库的元属性
     *
     * @return 返回数据库的元属性
     */
    @Override
    public DatabaseMetaData getDbMetaData() {
        DatabaseMetaData metaData = null;
        try {
            metaData = getConnection().getMetaData();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return metaData;
    }

    /**
     * 获取当前数据库类型
     *
     * @return 返回当前数据类型名称
     */
    @Override
    public String getCurDbType() {
        String dbType = "";
        try {
            dbType = getDbMetaData().getDatabaseProductName();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dbType.toLowerCase();
    }

    /**
     * 通过oracle数据库表名获取列名
     *
     * @param tableName 表名
     * @return 保存列名的map对象
     */
    @Override
    public Map getTableCols(String tableName) {
        Map mapCols = null;
        try {
            String dbType = getCurDbType();
            String sqlTableCols = "";
            if (dbType.equals("oracle"))
                sqlTableCols = "base.getOracleTableCols";
            else if (dbType.equals("mysql"))
                sqlTableCols = "";

            List list = getSqlMapClientTemplate().queryForList(sqlTableCols, tableName);
            for (Iterator it = list.iterator(); it.hasNext(); )
                mapCols.put(it.next(), "");

        } catch (Exception e) {
            logger.error(e);
        }
        return mapCols;
    }

    /**
     * 获取SqlMapClientTemplate对象
     *
     * @return 返回 SqlMapClientTemplate对象
     */
    @Override
     public SqlMapClientTemplate getSqlMapClientTpl() {
        return getSqlMapClientTemplate();
    }


    /**
     * 通过oracle数据库表名获取列名
     *
     * @param tableName 表名
     * @return 保存列名及注释的list集合
     */
    @Override
    public List getTableColumn(String tableName) {
        List list = null;
        try {
//            String dbType = getCurDbType();
//            String sqlTableCols = "";
//            if (dbType.equals("oracle"))
//                sqlTableCols = "base.getTableCols";
//            else if (dbType.equals("mysql"))
//                sqlTableCols = "";

//            list = getSqlMapClientTemplate().queryForList(sqlTableCols, tableName);
            list = getSqlMapClientTemplate().queryForList("base.getTableCols", tableName);
        } catch (Exception e) {
            logger.error(e);
        }
        return list;
    }

    /**
     * 通过表名获取一行记录的列名和默认值
     *
     * @param tableName 表名
     * @return 返回一行记录的列名和默认值
     */
    @Override
    public Map getTableColumnDefault(String tableName) {
        List list = getTableColumn(tableName.toUpperCase());
        Map mapTem, mapRet = new HashMap();

        for (Object obj : list) {
            mapTem = (Map) obj;
            mapRet.put(mapTem.get("Field"), mapTem.get("Default"));
        }
        return mapRet;
    }

    /**
     * 通过表名获取一行记录的列名和默认值
     *
     * @param tableName 表名
     * @param mapParam  一行记录值，覆盖默认值
     * @return 返回一行记录的列名和默认值
     */
    public Map getTableColumnDefault(String tableName, Map mapParam) {
        Map map = this.getTableColumnDefault(tableName);
        map.putAll(mapParam);
        return map;
    }

    /**
     * 原生执行指定的sqlMapId
     *
     * @param sqlMapId sqlmap的ID
     * @param param    参数对象
     * @return 返回执行结果, 需要自行转换类型
     * @throws DaoException 出错
     */
    public Object execStatement(String sqlMapId, Object param) throws DaoException {
        return getSqlMapClientTemplate().insert(sqlMapId, param);
    }

    /**
     * 动态语句查询
     *
     * @param dynamicSql 查询语句
     * @return 返回map对象集合, 否则返回空集合
     */
    @Override
    public Object getDynamicSql(String dynamicSql) {
        return getSqlMapClientTemplate().queryForList("base.getDynamicSql", dynamicSql);
    }


    /**
     * 根据sqlMapId添加新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws DaoException 出错
     */
    @Override
    public long insert(String sqlMapId, Map mapParam) throws DaoException {
        Long lRet;
        try {
            lRet = (Long) getSqlMapClientTemplate()
                    .insert(sqlMapId, mapParam);
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return lRet != null ? lRet.longValue() : 1;
    }

    /**
     * 根据sqlMapId批量添加新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回添加的记录数
     * @throws DaoException 出错
     */
    @Override
    public int insert(String sqlMapId, List listMap) throws DaoException {
        try {
            getSqlMapClientTemplate().getSqlMapClient().startTransaction();
            getSqlMapClientTemplate().getSqlMapClient().startBatch();
            for (Object mapParam : listMap) {
                getSqlMapClientTemplate()
                        .insert(sqlMapId, mapParam);
            }
            getSqlMapClientTemplate().getSqlMapClient().executeBatch();
            getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                getSqlMapClientTemplate().getSqlMapClient().endTransaction();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return listMap.size();
    }

    /**
     * 根据sqlMapId更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回更新的记录数
     * @throws DaoException 出错
     */
    @Override
    public int update(String sqlMapId, Map mapParam) throws DaoException {
        try {
            getSqlMapClientTemplate()
                    .update(sqlMapId, mapParam);

        } catch (Exception e) {
            throw new DaoException(e);
        } finally {
            try {
                getSqlMapClientTemplate().getSqlMapClient().endTransaction();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return 1;
    }

    /**
     * 根据sqlMapId批量更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回更新的记录数
     * @throws DaoException 出错
     */
    @Override
    public int update(String sqlMapId, List listMap) throws DaoException {
        try {
            getSqlMapClientTemplate().getSqlMapClient().startTransaction();
            getSqlMapClientTemplate().getSqlMapClient().startBatch();
            for (Object mapParam : listMap) {
                getSqlMapClientTemplate()
                        .update(sqlMapId, mapParam);
            }
            getSqlMapClientTemplate().getSqlMapClient().executeBatch();
            getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                getSqlMapClientTemplate().getSqlMapClient().endTransaction();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return listMap.size();
    }

    /**
     * 根据sqlMapId添加、更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws DaoException 出错
     */
    @Override
    public int save(String sqlMapId, Map mapParam) throws DaoException {
        Integer iRet = (Integer) getSqlMapClientTemplate()
                .insert(sqlMapId, mapParam);
        return iRet != null ? iRet : 1;
    }

    /**
     * 根据sqlMapId批量添加、更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回添加的记录数
     * @throws DaoException 出错
     */
    @Override
    public int save(String sqlMapId, List listMap) throws DaoException {
        try {
            getSqlMapClientTemplate().getSqlMapClient().startTransaction();
            getSqlMapClientTemplate().getSqlMapClient().startBatch();
            for (Object mapParam : listMap) {
                getSqlMapClientTemplate()
                        .insert(sqlMapId, mapParam);
            }
            getSqlMapClientTemplate().getSqlMapClient().executeBatch();
            getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                getSqlMapClientTemplate().getSqlMapClient().endTransaction();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return listMap.size();
    }

    /**
     * 根据sqlMapId动态参数查询
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam 包含各种查询条件,如果为null，查询所有记录
     * @return 获取查询记录
     */
    @Override
    public List select(String sqlMapId, Map mapParam) {
    	if (mapParam != null&& mapParam.get("page") != null) {
    		mapParam.put("CURPAGE", mapParam.get("page"));
		}
    	if (mapParam != null&& mapParam.get("pagesize") != null) {
    		mapParam.put("PAGESIZE", mapParam.get("pagesize"));
		}
    	
    	if (mapParam != null&& mapParam.get("sortname") != null) {
    		mapParam.put("SORTNAME", mapParam.get("sortname"));
    		mapParam.put("SORTORDER", "asc");
		}
    	
    	if (mapParam != null&& mapParam.get("sortorder") != null) {
    		mapParam.put("SORTORDER", mapParam.get("sortorder"));
		}
    	
    	
        if (mapParam != null&& mapParam.get("CURPAGE") != null) {
            int curPage = BaseTools.getMapByKey(mapParam, "CURPAGE").length() == 0 ? 1 : Integer.parseInt((String) mapParam.get("CURPAGE"));
            int pageSize = BaseTools.getMapByKey(mapParam, "PAGESIZE").length() == 0 ? 20 : Integer.parseInt((String) mapParam.get("PAGESIZE"));
            int rows = (curPage - 1) * pageSize;
            mapParam.put("ROWS", String.valueOf(rows));
        }
        return getSqlMapClientTemplate()
                .queryForList(sqlMapId, mapParam);
    }

    /**
     * 根据sqlMapId动态参数查询数值，比如记录总数
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam 包含各种查询条件
     * @return 获取查询数值
     */
    @Override
    public Object selectNum(String sqlMapId, Map mapParam) {
        return getSqlMapClientTemplate().queryForObject(sqlMapId, mapParam != null ? mapParam : new HashMap());
    }

    /**
     * 根据sqlMapId动态参数记录数
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam 包含各种查询条件,如果为null，查询记录总数
     * @return 获取查询记录数
     */
    @Override
    public int selectCnt(String sqlMapId, Map mapParam) {
        Integer iRet = (Integer) selectNum(sqlMapId, mapParam);
        return iRet != null ? iRet : 0;
    }

    /**
     * 根据sqlMapId动态参数查询,并返回指定的动态列
     * <Pre>
     * Map map = new HashMap();
     * 指定列名数组,例如:
     * List listCols = new ArrayList();
     * listCols.put("COL1");
     * listCOls.put("COL2 as OTHER");
     * map.put("COLS",listCols);
     * <p/>
     * 不需要分页时可以不写CURPAGE,PAGESIZE。
     * 需要分页时,例如:
     * map.put("CURPAGE", "1");
     * map.put("PAGESIZE", "20");
     * List list = get${sqlName}(map);
     * </Pre>
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam 包含各种查询条件(注意必须包含COLS)
     * @return 返回对象集合(注意:如果分页, 同时返回总记录数TOTAL), 否则返回空集合
     */
    @Override
    public List selectDynamic(String sqlMapId, Map mapParam) {
        return select(sqlMapId, mapParam);
    }

    /**
     * 根据sqlMapId删除纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回删除的记录数
     * @throws DaoException 出错
     */
    @Override
    public int delete(String sqlMapId, Map mapParam) throws DaoException {
        int iRet = 1;
        try {
            getSqlMapClient().delete(sqlMapId, mapParam);
        } catch (SQLException e) {
            iRet = -1;
            throw new DaoException(e.getMessage());
        }
        return iRet;
    }

    /**
     * 根据sqlMapId批量删除纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回删除的记录数
     * @throws DaoException 出错
     */
    @Override
    public int delete(String sqlMapId, List listMap) throws DaoException {
        try {
            getSqlMapClientTemplate().getSqlMapClient().startTransaction();
            getSqlMapClientTemplate().getSqlMapClient().startBatch();
            for (Object mapParam : listMap) {
                getSqlMapClientTemplate()
                        .delete(sqlMapId, mapParam);
            }
            getSqlMapClientTemplate().getSqlMapClient().executeBatch();
            getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                getSqlMapClientTemplate().getSqlMapClient().endTransaction();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return listMap.size();
    }

    /**
     * 根据sqlMapId执行存储过程或函数
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象集合
     * @return 返回执行结果
     * @throws DaoException 出错
     */
    @Override
    public Map procedure(String sqlMapId, Map mapParam) throws DaoException {
        try {
            getSqlMapClientTemplate()
                    .insert(sqlMapId, mapParam);

        } catch (Exception e) {
            throw new DaoException(e);
        }
        return mapParam;
    }

    /**
     * 添加新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws DaoException 出错
     */
    public long insert(Map mapParam) throws DaoException {
        return insert(getSqlmapNamespace() + SQLID_INSERT, mapParam);
    }

    /**
     * 批量添加新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws DaoException 出错
     */
    public int insert(List listMap) throws DaoException {
        return insert(getSqlmapNamespace() + SQLID_INSERT, listMap);
    }

    /**
     * 更新纪录
     *
     * @param mapParam map对象
     * @return 返回更新的记录数
     * @throws DaoException 出错
     */
    public int update(Map mapParam) throws DaoException {
        return update(getSqlmapNamespace() + SQLID_UPDATE, mapParam);
    }

    /**
     * 批量更新纪录
     *
     * @param listMap map对象集合
     * @return 返回更新的记录数
     * @throws DaoException 出错
     */
    public int update(List listMap) throws DaoException {
        return update(getSqlmapNamespace() + SQLID_UPDATE, listMap);
    }

    /**
     * 添加、更新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws DaoException 出错
     */
    public int save(Map mapParam) throws DaoException {
        return save(getSqlmapNamespace() + SQLID_SAVE, mapParam);
    }

    /**
     * 批量添加、更新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws DaoException 出错
     */
    public int save(List listMap) throws DaoException {
        return save(getSqlmapNamespace() + SQLID_SAVE, listMap);
    }


    /**
     * 动态参数查询
     *
     * @param mapParam 包含各种查询条件,如果为null，查询所有记录
     * @return 获取查询记录
     */
    public List select(Map mapParam) {
        return select(getSqlmapNamespace() + SQLID_SELECT, mapParam);
    }


    /**
     * 删除纪录
     *
     * @param mapParam map对象
     * @return 返回删除的记录数
     * @throws DaoException 出错
     */
    public int delete(Map mapParam) throws DaoException {
        return delete(getSqlmapNamespace() + SQLID_DELETE, mapParam);
    }

    /**
     * 批量删除纪录
     *
     * @param listMap map对象集合
     * @return 返回删除的记录数
     * @throws DaoException 出错
     */
    public int delete(List listMap) throws DaoException {
        return delete(getSqlmapNamespace() + SQLID_DELETE, listMap);
    }
}

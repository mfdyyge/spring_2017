package com.base.core.bo;

import com.base.core.dao.IDao;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * <li>创建文档 Date: 2014-09-01</li>
 * <li>Bo层核心类，提供通用业务操作
 * <pre>
 *     使用说明：
 *     1.在spring中注入daoBase对象；
 *     2.业务类可以继承，自动获取通用Bo方法，无需再编写代码；
 * </pre>
 * </li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
@Component("boBase")
public class BoBase implements IBo {
    private Logger logger = Logger.getLogger(this.getClass());
    @Resource(name="daoBase")
    protected IDao iDao;

    public IDao getiDao() {
        return iDao;
    }

    public void setiDao(IDao iDao) {
        this.iDao = iDao;
    }

    public BoBase() {
    }

    /**
     * 返回当前数据库连接对象
     *
     * @return 连接对象
     */
    @Override
    public Connection getConnection() {
        return iDao.getConnection();
    }

    /**
     * 获取数据库的元属性
     *
     * @return 返回数据库的元属性
     */
    @Override
    public DatabaseMetaData getDbMetaData() {
        return iDao.getDbMetaData();
    }

    /**
     * 获取当前数据库类型
     *
     * @return 返回当前数据类型名称
     */
    @Override
    public String getCurDbType() {
        return iDao.getCurDbType();
    }

    /**
     * 通过oracle数据库表名获取列名
     *
     * @param tableName 表名
     * @return 保存列名的map对象
     */
    @Override
    public Map getTableCols(String tableName) {
        return iDao.getTableCols(tableName);
    }

    /**
     * 动态语句查询
     *
     * @param dynamicSql 查询语句
     * @return 返回map对象集合, 否则返回空集合
     */
    @Override
    public Object getDynamicSql(String dynamicSql) {
        return iDao.getDynamicSql(dynamicSql);
    }

    /**
     * 通过oracle数据库表名获取列名
     *
     * @param tableName 表名
     * @return 保存列名及注释的list集合
     */
    @Override
    public List getTableColumn(String tableName) {
        return iDao.getTableColumn(tableName);
    }

    /**
     * 原生执行指定的sqlMapId
     *
     * @param sqlMapId sqlmap的ID
     * @param param    参数对象
     * @return 返回执行结果, 需要自行转换类型
     */
    public Object execStatement(String sqlMapId, Object param) throws BoException {
        try {
            return iDao.execStatement(sqlMapId, param);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new BoException(e);
        }
    }

    /**
     * 根据sqlMapId添加新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     */
    @Override
    public long insert(String sqlMapId, Map mapParam) throws SaveException {
        try {
            return iDao.insert(sqlMapId, mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }
    }

    /**
     * 根据sqlMapId批量添加新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    @Override
    public int insert(String sqlMapId, List listMap) throws SaveException {
        int iRet;
        try {
            iRet = iDao.insert(sqlMapId, listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }
        return iRet;
    }

    /**
     * 根据sqlMapId更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回更新的记录数
     * @throws SaveException 出错
     */
    @Override
    public int update(String sqlMapId, Map mapParam) throws SaveException {
        int iRet;
        try {
            iRet = iDao.update(sqlMapId, mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }
        return iRet;
    }

    /**
     * 根据sqlMapId批量更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回更新的记录数
     * @throws SaveException 出错
     */
    @Override
    public int update(String sqlMapId, List listMap) throws SaveException {
        int iRet;
        try {
            iRet = iDao.update(sqlMapId, listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }
        return iRet;
    }

    /**
     * 根据sqlMapId添加、更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    @Override
    public int save(String sqlMapId, Map mapParam) throws SaveException {
        int iRet;
        try {
            iRet = iDao.save(sqlMapId, mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
    }

    /**
     * 根据sqlMapId批量添加、更新纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    @Override
    public int save(String sqlMapId, List listMap) throws SaveException {
        int iRet;
        try {
            iRet = iDao.save(sqlMapId, listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
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
        List retList;
        try {
            retList = iDao.select(sqlMapId, mapParam);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceLocatorException(e);
        }

        return retList;
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
        return iDao.selectNum(sqlMapId, mapParam);
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
     * @throws DelErrorException 出错
     */
    @Override
    public int delete(String sqlMapId, Map mapParam) throws DelErrorException {
        int iRet;
        try {
            iRet = iDao.delete(sqlMapId, mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new DelErrorException(e);
        }

        return iRet;
    }

    /**
     * 根据sqlMapId批量删除纪录
     *
     * @param sqlMapId sqlmap的ID
     * @param listMap  map对象集合
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    @Override
    public int delete(String sqlMapId, List listMap) throws DelErrorException {
        int iRet;
        try {
            iRet = iDao.delete(sqlMapId, listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new DelErrorException(e);
        }

        return iRet;
    }

    /**
     * 根据sqlMapId执行存储过程或函数
     *
     * @param sqlMapId sqlmap的ID
     * @param mapParam map对象集合
     * @return 返回执行结果
     * @throws ProOrFunException 出错
     */
    @Override
    public Map procedure(String sqlMapId, Map mapParam) throws ProOrFunException {
        try {
            mapParam = iDao.procedure(sqlMapId, mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new ProOrFunException(e);
        }
        return mapParam;
    }

    /**
     * 添加新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    public long insert(Map mapParam) throws SaveException {
        try {
            return iDao.insert(mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }
    }

    /**
     * 批量添加新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    public int insert(List listMap) throws SaveException {
        int iRet;
        try {
            iRet = iDao.insert(listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
    }

    /**
     * 更新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws com.base.core.domain.exception.SaveException 出错
     */
    @Override
    public int update(Map mapParam) throws SaveException {
        int iRet;
        try {
            iRet = iDao.update(mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
    }

    /**
     * 批量更新纪录
     *
     * @param listMap map对象集合
     * @return 返回更新的记录数
     * @throws SaveException 出错
     */
    public int update(List listMap) throws SaveException {
        int iRet;
        try {
            iRet = iDao.update(listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
    }

    /**
     * 添加、更新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    public int save(Map mapParam) throws SaveException {
        int iRet;
        try {
            iRet = iDao.save(mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
    }

    /**
     * 批量添加、更新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    public int save(List listMap) throws SaveException {
        int iRet;
        try {
            iRet = iDao.save(listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new SaveException(e);
        }

        return iRet;
    }


    /**
     * 动态参数查询
     *
     * @param mapParam 包含各种查询条件,如果为null，查询所有记录
     * @return 获取查询记录
     */
    public List select(Map mapParam) {
        List retList;
        try {
            retList = iDao.select(mapParam);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceLocatorException(e);
        }

        return retList;
    }

    /**
     * 删除纪录
     *
     * @param mapParam map对象
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    public int delete(Map mapParam) throws DelErrorException {
        int iRet;
        try {
            iRet = iDao.delete(mapParam);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new DelErrorException(e);
        }

        return iRet;
    }

    /**
     * 批量删除纪录
     *
     * @param listMap map对象集合
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    public int delete(List listMap) throws DelErrorException {
        int iRet;
        try {
            iRet = iDao.delete(listMap);
        } catch (DaoException e) {
            logger.error(e.getMessage());
            throw new DelErrorException(e);
        }

        return iRet;
    }
}

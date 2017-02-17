package com.base.auth.dao.impl;

import com.base.core.dao.DaoBase;
import com.base.core.domain.exception.DaoException;
import com.base.core.domain.tools.BaseTools;
import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;
import java.util.*;
import com.base.auth.dao.IDaoSYS_USER;

/**
 * <ol>
 * date:2016-12-20 editor:dingshuangbo
 * <li>创建文档</li>
 * <li>用户表数据操作接口实现类</li>
 * </ol>
 *
 * @author <a href="mailto:2449709309@qq.com">dingshuangbo</a>
 * @version 2.0
 * @since 1.6
 */
@Repository("daoSYS_USER")
public final class DaoSYS_USERImpl extends DaoBase
        implements IDaoSYS_USER {
	private Logger log = Logger.getLogger(this.getClass());

    public DaoSYS_USERImpl(){
	}

	/**
	 * 添加用户表记录
	 * @param mapParam 用户表map对象集合
	 * @return 返回添加记录的序列, 如果没有序列返回1
	 * @throws DaoException 出错
	 */
	public long insertSYS_USER(Map mapParam) throws DaoException{
        mapParam.put("ID", BaseTools.getNextSeq()); 
        return insert("auth.insertSYS_USER", mapParam);
	}

	/**
	 * 动态修改用户表记录
	 * @param mapParam 用户表map对象集合
	 * @return 返回成功标志 1为成功 0为不成功
	 * @throws DaoException 出错
	 */
	public int updateSYS_USER(Map mapParam) throws DaoException{
        return update("auth.updateSYS_USER", mapParam);
	}

	/**
	 * 查询用户表记录
     * <Pre>
     * 不需要分页时可以不写CURPAGE,PAGESIZE。
     * 需要分页时,例如:
     * Map map = new HashMap();
     * map.put("CURPAGE", "1");
     * map.put("PAGESIZE", "20");
     * List list = getSYS_USER(map);
     * </Pre>
     *
  	 * @param mapParam 用户表查询条件
	 * @return 返回用户表map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
	 * @throws DaoException 出错
	 */
	public List selectSYS_USER(Map mapParam) throws DaoException{
        return select("auth.selectSYS_USER", mapParam);
	}
   
	/**
	 * 查询用户表记录数
	 * @param mapParam 用户表查询条件
	 * @return 返回用户表记录数
	 * @throws DaoException 出错
	 */
	public int selectSYS_USERCount(Map mapParam) throws DaoException{
        return selectCnt("auth.selectSYS_USERCount", mapParam != null ? mapParam : new HashMap());
	}

    /**
    * 查询用户表记录返回指定的动态列
    * <Pre>
    * Map map = new HashMap();
    * 指定列名数组,例如:
    * List listCols = new ArrayList();
    * listCols.add("COL1");
    * listCols.add("COL2 as OTHER");
    * map.put("COLS",listCols);
    *
    * 不需要分页时可以不写CURPAGE,PAGESIZE。
    * 需要分页时,例如:
    * map.put("CURPAGE", "1");
    * map.put("PAGESIZE", "20");
    * List list = getSYS_USER(map);
    * </Pre>
    *
    * @param mapParam 用户表查询条件
    * @return 返回用户表map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    * @throws DaoException 出错
    */
    public List selectDynamicSYS_USER(Map mapParam) throws DaoException{
        return select("auth.selectDynamicSYS_USER", mapParam);
    }
   
	/**
	 * 删除用户表记录
	 * @param mapParam 用户表删除map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DaoException 出错
	 */
	public int deleteSYS_USER(Map mapParam) throws DaoException{
        return delete("auth.deleteSYS_USER", mapParam);
	}
}

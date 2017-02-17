package com.base.auth.bo.impl;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.base.core.bo.BoBase;
import com.base.core.domain.exception.DaoException;
import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.SaveException;
import com.base.core.domain.exception.ServiceLocatorException;
import com.base.core.domain.tools.BaseTools;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.base.auth.bo.IBoSYS_USER;
import com.base.auth.dao.IDaoSYS_USER;
/**
 * <ol>
 * date:2016-12-20 editor:dingshuangbo
 * <li>创建文档</li>
 * <li>用户表业务处理接口实现类</li>
 * </ol>
 *
 * @author <a href="mailto:2449709309@qq.com">dingshuangbo</a>
 * @version 2.0
 * @since 1.6
 */
@Component(value="boSYS_USER")
public class BoSYS_USERImpl extends BoBase
        implements IBoSYS_USER {
	private Logger log = Logger.getLogger(this.getClass());
    @Autowired
	private IDaoSYS_USER iDao;

    public BoSYS_USERImpl(){

	}

	/**
	 * 保存用户表记录
	 * @param mapParam 用户表map对象
	 * @return 返回添加记录的序列,如果没有序列返回1
	 * @throws SaveException 出错
	 */
	public long insertSYS_USER(Map mapParam) throws SaveException{
		try {
            return iDao.insertSYS_USER(mapParam);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new SaveException(e.getMessage(), e);
        }
	}

    /**
    * 动态修改用户表记录
    * @param mapParam 用户表map对象
    * @return 返回操作影响数 0为不成功
    * @throws SaveException 出错
    */
    public int updateSYS_USER(Map mapParam) throws SaveException{
    	try {
			//判断是否批量修改
			String idStr = MapUtils.getString(mapParam, "IDS","");
			if(StringUtils.isNotEmpty(idStr)){
				String[] ids=idStr.split(",");
				for (int i = 0; i < ids.length; i++) {
					mapParam.put("ID", ids[i]);
					if (BaseTools.isEmpty(mapParam.get("ID")))
						throw new SaveException("用户ID不允许为空!");
					
					iDao.updateSYS_USER(mapParam);
				}
				return 1;
			}else {
	            if (BaseTools.isEmpty(mapParam.get("ID")))
	                throw new SaveException("用户ID不允许为空!");
				return iDao.updateSYS_USER(mapParam);
			}
		} catch (DaoException e) {
			throw new SaveException(e.getMessage());
		}
    }
    
	/**
	 * 批量动态修改用户表记录
	 * @param listMap 用户表map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws SaveException 出错
	 */
	public int updateSYS_USER(List listMap) throws SaveException{
        int iRet = 0;
		Map mapTem;
		try{
            for(Iterator it = listMap.iterator(); it.hasNext(); ) {
				mapTem = (Map)it.next();
                if (BaseTools.isEmpty(mapTem.get("ID")))
                    throw new SaveException("用户ID不允许为空!");

                iRet += iDao.updateSYS_USER(mapTem);
            }
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new SaveException(e.getMessage(), e);
        }
        return iRet;
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
    * @param mapParam 用户表实体
    * @return 返回用户表map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    */
	public List selectSYS_USER(Map mapParam){
		List list;
		try {
            list = iDao.selectSYS_USER(mapParam);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceLocatorException(e.getMessage(), e);
        }
		return list;
	}
    
    /**
    * 查询用户表记录返回指定的动态列
    * <Pre>
    * Map map = new HashMap();
    * 指定列名数组,例如:
    * List listCols = new ArrayList();
    * listCols.add("COL1");
    * listCOls.add("COL2 as OTHER");
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
    */
    public List selectDynamicSYS_USER(Map mapParam){
        List list;
        try {
            list = iDao.selectDynamicSYS_USER(mapParam);
        } catch (DaoException e) {
        log.error(e.getMessage());
        throw new ServiceLocatorException(e.getMessage(), e);
        }
        return list;
    }

	/**
	 * 查询用户表记录数
	 * @param mapParam 用户表查询参数
	 * @return 返回用户表记录数
	 */
	public int selectSYS_USERCount(Map mapParam){
		try {
            return iDao.selectSYS_USERCount(mapParam);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceLocatorException(e.getMessage(), e);
        }
	}
    
    /**
    * 删除用户表记录
    * @param mapParam 用户表map对象
    * @return 返回操作影响数 0为不成功
    * @throws DelErrorException 出错
    */
    public int deleteSYS_USER(Map mapParam) throws DelErrorException{
        try {
            if (BaseTools.isEmpty(mapParam.get("ID")))
                throw new DelErrorException("用户ID不允许为空!");
            return iDao.deleteSYS_USER(mapParam);
        } catch (DaoException e) {
        throw new DelErrorException(e.getMessage());
        }
    }

	/**
	 * 批量删除用户表记录
	 * @param listMap 用户表map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DelErrorException 出错
	 */
	public int deleteSYS_USER(List listMap) throws DelErrorException{
		int iRet = 0;
		Map mapTem;
		try {
            for (Object aListMap : listMap) {
                mapTem = (Map) aListMap;
                if (BaseTools.isEmpty(mapTem.get("ID")))
                    throw new DelErrorException("用户ID不允许为空!");

                iRet += iDao.deleteSYS_USER(mapTem);
            }
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new DelErrorException(e.getMessage(), e);
        }
		return iRet;
	}
}

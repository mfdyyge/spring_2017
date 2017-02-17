package com.base.auth.service.impl;

import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.SaveException;
import com.base.core.service.ServiceBase;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.base.auth.bo.IBoSYS_USER;
import com.base.auth.service.IServiceSYS_USER;
/**
 * <ol>
 * date:2016-12-20 editor:dingshuangbo
 * <li>创建文档</li>
 * <li>用户表服务接口实现类</li>
 * </ol>
 *
 * @author <a href="mailto:2449709309@qq.com">dingshuangbo</a>
 * @version 2.0
 * @since 1.6
 */
@Service(value="serviceSYS_USER")
public class ServiceSYS_USERImpl extends ServiceBase
        implements IServiceSYS_USER {
	private Logger log = Logger.getLogger(this.getClass());
    @Autowired
	private IBoSYS_USER iBo;

    public ServiceSYS_USERImpl(){

	}

	/**
	 * 保存用户表记录
	 * @param mapParam 用户表map对象集合
	 * @return 返回添加记录的序列,如果没有序列返回1
	 * @throws SaveException 出错
	 */
	public long insertSYS_USER(Map mapParam) throws SaveException{
        return iBo.insertSYS_USER(mapParam);
	}

    /**
    * 动态修改用户表记录
    * @param mapParam 用户表map对象
    * @return 返回操作影响数 0为不成功
    * @throws SaveException 出错
    */
    public int updateSYS_USER(Map mapParam) throws SaveException{
        return iBo.updateSYS_USER(mapParam);
    }
    
	/**
	 * 批量动态修改用户表记录
	 * @param listMap 用户表map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws SaveException 出错
	 */
	public int updateSYS_USER(List listMap) throws SaveException{
        return iBo.updateSYS_USER(listMap);
	}
    
    /**
    * 查询用户表记录
    * <Pre>
    * 不需要分页时可以不写CURPAGE,PAGESIZE。
    * 需要分页时,例如:
    * Map map = new HashMap();
    * map.put("CURPAGE", "1");
    * map.put("PAGESIZE", "20");
    * List list = selectSYS_USER(map);
    * </Pre>
    *
    * @param mapParam 用户表实体
    * @return 返回用户表map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    */
	public List selectSYS_USER(Map mapParam){
		return iBo.selectSYS_USER(mapParam);
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
    * List list = selectDynamicSYS_USER(map);
    * </Pre>
    *
    * @param mapParam 用户表查询条件
    * @return 返回用户表map对象集合(注意:同时返回总记录数TOTAL),否则返回空集合
    */
    public List selectDynamicSYS_USER(Map mapParam){
        return iBo.selectDynamicSYS_USER(mapParam);
    }

	/**
	 * 查询用户表记录数
	 * @param mapParam 用户表查询参数
	 * @return 返回用户表记录数
	 */
	public int selectSYS_USERCount(Map mapParam){
        return iBo.selectSYS_USERCount(mapParam);
	}
    
    /**
    * 删除用户表记录
    * @param mapParam 用户表map对象
    * @return 返回操作影响数 0为不成功
    * @throws DelErrorException 出错
    */
    public int deleteSYS_USER(Map mapParam) throws DelErrorException{
        return iBo.deleteSYS_USER(mapParam);
    }

	/**
	 * 批量删除用户表记录
	 * @param listMap 用户表map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DelErrorException 出错
	 */
	public int deleteSYS_USER(List listMap) throws DelErrorException{
		return iBo.deleteSYS_USER(listMap);
	}
}

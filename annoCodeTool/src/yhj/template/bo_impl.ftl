package ${mapProp.boPath}.impl;

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
import ${mapProp.boPath}.IBo${sqlName};
import ${mapProp.daoPath}.IDao${sqlName};
/**
 * <ol>
 * date:${date} editor:${mapProp.editor}
 * <li>创建文档</li>
 * <li>${tabelComments}业务处理接口实现类</li>
 * </ol>
 *
 * @author <a href="${mapProp.author}">${mapProp.editor}</a>
 * @version 2.0
 * @since 1.6
 */
@Component(value="bo${sqlName}")
public class Bo${sqlName}Impl extends BoBase
        implements IBo${sqlName} {
	private Logger log = Logger.getLogger(this.getClass());
    @Autowired
	private IDao${sqlName} iDao;

	<#--public Bo${sqlName}Impl(IDao iDao){-->
		<#--this.iDao = (IDao${sqlName})iDao;-->
	<#--}-->
    public Bo${sqlName}Impl(){

	}

	/**
	 * 保存${tabelComments}记录
	 * @param mapParam ${tabelComments}map对象
	 * @return 返回添加记录的序列,如果没有序列返回1
	 * @throws SaveException 出错
	 */
	public long insert${sqlName}(Map mapParam) throws SaveException{
		try {
            return iDao.insert${sqlName}(mapParam);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new SaveException(e.getMessage(), e);
        }
	}

    /**
    * 动态修改${tabelComments}记录
    * @param mapParam ${tabelComments}map对象
    * @return 返回操作影响数 0为不成功
    * @throws SaveException 出错
    */
    public int update${sqlName}(Map mapParam) throws SaveException{
    	try {
			//判断是否批量修改
			String idStr = MapUtils.getString(mapParam, "IDS","");
			if(StringUtils.isNotEmpty(idStr)){
				String[] ids=idStr.split(",");
				for (int i = 0; i < ids.length; i++) {
				<#list mapPriKey?keys as col>
					mapParam.put("${col}", ids[i]);
					if (BaseTools.isEmpty(mapParam.get("${col}")))
						throw new SaveException("${mapPriKey[col]}不允许为空!");
				 </#list>
					
					iDao.update${sqlName}(mapParam);
				}
				return 1;
			}else {
				<#list mapPriKey?keys as col>
	            if (BaseTools.isEmpty(mapParam.get("${col}")))
	                throw new SaveException("${mapPriKey[col]}不允许为空!");
	            </#list>
				return iDao.update${sqlName}(mapParam);
			}
		} catch (DaoException e) {
			throw new SaveException(e.getMessage());
		}
    }
    
	/**
	 * 批量动态修改${tabelComments}记录
	 * @param listMap ${tabelComments}map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws SaveException 出错
	 */
	public int update${sqlName}(List listMap) throws SaveException{
        int iRet = 0;
		Map mapTem;
		try{
            for(Iterator it = listMap.iterator(); it.hasNext(); ) {
				mapTem = (Map)it.next();
				<#list mapPriKey?keys as col>
                if (BaseTools.isEmpty(mapTem.get("${col}")))
                    throw new SaveException("${mapPriKey[col]}不允许为空!");
                </#list>

                iRet += iDao.update${sqlName}(mapTem);
            }
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new SaveException(e.getMessage(), e);
        }
        return iRet;
	}
    
    /**
    * 查询${tabelComments}记录
    * <Pre>
    * 不需要分页时可以不写CURPAGE,PAGESIZE。
    * 需要分页时,例如:
    * Map map = new HashMap();
    * map.put("CURPAGE", "1");
    * map.put("PAGESIZE", "20");
    * List list = get${sqlName}(map);
    * </Pre>
    *
    * @param mapParam ${tabelComments}实体
    * @return 返回${tabelComments}map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    */
	public List select${sqlName}(Map mapParam){
		List list;
		try {
            list = iDao.select${sqlName}(mapParam);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceLocatorException(e.getMessage(), e);
        }
		return list;
	}
    
    /**
    * 查询${tabelComments}记录返回指定的动态列
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
    * List list = get${sqlName}(map);
    * </Pre>
    *
    * @param mapParam ${tabelComments}查询条件
    * @return 返回${tabelComments}map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    */
    public List selectDynamic${sqlName}(Map mapParam){
        List list;
        try {
            list = iDao.selectDynamic${sqlName}(mapParam);
        } catch (DaoException e) {
        log.error(e.getMessage());
        throw new ServiceLocatorException(e.getMessage(), e);
        }
        return list;
    }

	/**
	 * 查询${tabelComments}记录数
	 * @param mapParam ${tabelComments}查询参数
	 * @return 返回${tabelComments}记录数
	 */
	public int select${sqlName}Count(Map mapParam){
		try {
            return iDao.select${sqlName}Count(mapParam);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceLocatorException(e.getMessage(), e);
        }
	}
    
    /**
    * 删除${tabelComments}记录
    * @param mapParam ${tabelComments}map对象
    * @return 返回操作影响数 0为不成功
    * @throws DelErrorException 出错
    */
    public int delete${sqlName}(Map mapParam) throws DelErrorException{
        try {
            <#list mapPriKey?keys as col>
            if (BaseTools.isEmpty(mapParam.get("${col}")))
                throw new DelErrorException("${mapPriKey[col]}不允许为空!");
            </#list>
            return iDao.delete${sqlName}(mapParam);
        } catch (DaoException e) {
        throw new DelErrorException(e.getMessage());
        }
    }

	/**
	 * 批量删除${tabelComments}记录
	 * @param listMap ${tabelComments}map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DelErrorException 出错
	 */
	public int delete${sqlName}(List listMap) throws DelErrorException{
		int iRet = 0;
		Map mapTem;
		try {
            for (Object aListMap : listMap) {
                mapTem = (Map) aListMap;
				<#list mapPriKey?keys as col>
                if (BaseTools.isEmpty(mapTem.get("${col}")))
                    throw new DelErrorException("${mapPriKey[col]}不允许为空!");
                </#list>

                iRet += iDao.delete${sqlName}(mapTem);
            }
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new DelErrorException(e.getMessage(), e);
        }
		return iRet;
	}
}

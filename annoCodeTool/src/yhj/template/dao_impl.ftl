package ${mapProp.daoPath}.impl;

import com.base.core.dao.DaoBase;
import com.base.core.domain.exception.DaoException;
import com.base.core.domain.tools.BaseTools;
import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;
import java.util.*;
import ${mapProp.daoPath}.IDao${sqlName};

/**
 * <ol>
 * date:${date} editor:${mapProp.editor}
 * <li>创建文档</li>
 * <li>${tabelComments}数据操作接口实现类</li>
 * </ol>
 *
 * @author <a href="${mapProp.author}">${mapProp.editor}</a>
 * @version 2.0
 * @since 1.6
 */
@Repository("dao${sqlName}")
public final class Dao${sqlName}Impl extends DaoBase
        implements IDao${sqlName} {
	private Logger log = Logger.getLogger(this.getClass());
	<#--public Dao${sqlName}Impl(SqlMapClient sqlMap){-->
		<#--super(sqlMap);-->
	<#--}-->

    public Dao${sqlName}Impl(){
	}

	/**
	 * 添加${tabelComments}记录
	 * @param mapParam ${tabelComments}map对象集合
	 * @return 返回添加记录的序列, 如果没有序列返回1
	 * @throws DaoException 出错
	 */
	public long insert${sqlName}(Map mapParam) throws DaoException{
		<#list mapPriKey?keys as col>
        mapParam.put("${col}", BaseTools.getNextSeq()); 
        </#list>
        return insert("${mapProp.sqlmap_namespace}.insert${sqlName}", mapParam);
	}

	/**
	 * 动态修改${tabelComments}记录
	 * @param mapParam ${tabelComments}map对象集合
	 * @return 返回成功标志 1为成功 0为不成功
	 * @throws DaoException 出错
	 */
	public int update${sqlName}(Map mapParam) throws DaoException{
        return update("${mapProp.sqlmap_namespace}.update${sqlName}", mapParam);
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
  	 * @param mapParam ${tabelComments}查询条件
	 * @return 返回${tabelComments}map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
	 * @throws DaoException 出错
	 */
	public List select${sqlName}(Map mapParam) throws DaoException{
        return select("${mapProp.sqlmap_namespace}.select${sqlName}", mapParam);
	}
   
	/**
	 * 查询${tabelComments}记录数
	 * @param mapParam ${tabelComments}查询条件
	 * @return 返回${tabelComments}记录数
	 * @throws DaoException 出错
	 */
	public int select${sqlName}Count(Map mapParam) throws DaoException{
        return selectCnt("${mapProp.sqlmap_namespace}.select${sqlName}Count", mapParam != null ? mapParam : new HashMap());
	}

    /**
    * 查询${tabelComments}记录返回指定的动态列
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
    * List list = get${sqlName}(map);
    * </Pre>
    *
    * @param mapParam ${tabelComments}查询条件
    * @return 返回${tabelComments}map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    * @throws DaoException 出错
    */
    public List selectDynamic${sqlName}(Map mapParam) throws DaoException{
        return select("${mapProp.sqlmap_namespace}.selectDynamic${sqlName}", mapParam);
    }
   
	/**
	 * 删除${tabelComments}记录
	 * @param mapParam ${tabelComments}删除map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DaoException 出错
	 */
	public int delete${sqlName}(Map mapParam) throws DaoException{
        return delete("${mapProp.sqlmap_namespace}.delete${sqlName}", mapParam);
	}
}

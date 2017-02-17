package ${mapProp.servicePath}.impl;

import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.SaveException;
import com.base.core.service.ServiceBase;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import ${mapProp.boPath}.IBo${sqlName};
import ${mapProp.servicePath}.IService${sqlName};
/**
 * <ol>
 * date:${date} editor:${mapProp.editor}
 * <li>创建文档</li>
 * <li>${tabelComments}服务接口实现类</li>
 * </ol>
 *
 * @author <a href="${mapProp.author}">${mapProp.editor}</a>
 * @version 2.0
 * @since 1.6
 */
@Service(value="service${sqlName}")
public class Service${sqlName}Impl extends ServiceBase
        implements IService${sqlName} {
	private Logger log = Logger.getLogger(this.getClass());
    @Autowired
	private IBo${sqlName} iBo;

	<#--public Bo${sqlName}Impl(iBo iBo){-->
		<#--this.iBo = (iService${sqlName})iBo;-->
	<#--}-->
    public Service${sqlName}Impl(){

	}

	/**
	 * 保存${tabelComments}记录
	 * @param mapParam ${tabelComments}map对象集合
	 * @return 返回添加记录的序列,如果没有序列返回1
	 * @throws SaveException 出错
	 */
	public long insert${sqlName}(Map mapParam) throws SaveException{
        return iBo.insert${sqlName}(mapParam);
	}

    /**
    * 动态修改${tabelComments}记录
    * @param mapParam ${tabelComments}map对象
    * @return 返回操作影响数 0为不成功
    * @throws SaveException 出错
    */
    public int update${sqlName}(Map mapParam) throws SaveException{
        return iBo.update${sqlName}(mapParam);
    }
    
	/**
	 * 批量动态修改${tabelComments}记录
	 * @param listMap ${tabelComments}map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws SaveException 出错
	 */
	public int update${sqlName}(List listMap) throws SaveException{
        return iBo.update${sqlName}(listMap);
	}
    
    /**
    * 查询${tabelComments}记录
    * <Pre>
    * 不需要分页时可以不写CURPAGE,PAGESIZE。
    * 需要分页时,例如:
    * Map map = new HashMap();
    * map.put("CURPAGE", "1");
    * map.put("PAGESIZE", "20");
    * List list = select${sqlName}(map);
    * </Pre>
    *
    * @param mapParam ${tabelComments}实体
    * @return 返回${tabelComments}map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    */
	public List select${sqlName}(Map mapParam){
		return iBo.select${sqlName}(mapParam);
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
    * List list = selectDynamic${sqlName}(map);
    * </Pre>
    *
    * @param mapParam ${tabelComments}查询条件
    * @return 返回${tabelComments}map对象集合(注意:同时返回总记录数TOTAL),否则返回空集合
    */
    public List selectDynamic${sqlName}(Map mapParam){
        return iBo.selectDynamic${sqlName}(mapParam);
    }

	/**
	 * 查询${tabelComments}记录数
	 * @param mapParam ${tabelComments}查询参数
	 * @return 返回${tabelComments}记录数
	 */
	public int select${sqlName}Count(Map mapParam){
        return iBo.select${sqlName}Count(mapParam);
	}
    
    /**
    * 删除${tabelComments}记录
    * @param mapParam ${tabelComments}map对象
    * @return 返回操作影响数 0为不成功
    * @throws DelErrorException 出错
    */
    public int delete${sqlName}(Map mapParam) throws DelErrorException{
        return iBo.delete${sqlName}(mapParam);
    }

	/**
	 * 批量删除${tabelComments}记录
	 * @param listMap ${tabelComments}map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DelErrorException 出错
	 */
	public int delete${sqlName}(List listMap) throws DelErrorException{
		return iBo.delete${sqlName}(listMap);
	}
}

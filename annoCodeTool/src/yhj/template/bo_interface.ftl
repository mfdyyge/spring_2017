package ${mapProp.boPath};

import com.base.core.bo.IBo;
import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.SaveException;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * date:${date} editor:${mapProp.editor}
 * <li>创建文档</li>
 * <li>${tabelComments}业务处理接口类</li>
 * </ol>
 *
 * @author <a href="${mapProp.author}">${mapProp.editor}</a>
 * @version 2.0
 * @since 1.6
 */
public interface IBo${sqlName} extends IBo {
	/**
	 * 保存${tabelComments}记录
	 * @param mapParam ${tabelComments}map对象集合
	 * @return 返回添加记录的序列,如果没有序列返回1
	 * @throws SaveException 出错
	 */
	public long insert${sqlName}(Map mapParam) throws SaveException;

    /**
    * 动态修改${tabelComments}记录
    * @param mapParam ${tabelComments}map对象
    * @return 返回操作影响数 0为不成功
    * @throws SaveException 出错
    */
    public int update${sqlName}(Map mapParam) throws SaveException;
    
	/**
	 * 批量动态修改${tabelComments}记录
	 * @param listMap ${tabelComments}map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws SaveException 出错
	 */
	public int update${sqlName}(List listMap) throws SaveException;
    
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
	public List select${sqlName}(Map mapParam);
    
    /**
    * 查询${tabelComments}记录数
    * @param mapParam ${tabelComments}查询参数
    * @return 返回${tabelComments}记录数
    */
    public int select${sqlName}Count(Map mapParam);

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
    * @return 返回${tabelComments}map对象集合(注意:如果分页,同时返回总记录数TOTAL),否则返回空集合
    */
    public List selectDynamic${sqlName}(Map mapParam);
    
    /**
    * 删除${tabelComments}记录
    * @param mapParam ${tabelComments}map对象
    * @return 返回操作影响数 0为不成功
    * @throws DelErrorException 出错
    */
    public int delete${sqlName}(Map mapParam) throws DelErrorException;

	/**
	 * 批量删除${tabelComments}记录
	 * @param listMap ${tabelComments}map对象集合
	 * @return 返回操作影响数 0为不成功
	 * @throws DelErrorException 出错
	 */
	public int delete${sqlName}(List listMap) throws DelErrorException;
}

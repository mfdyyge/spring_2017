package com.base.core.service;

import com.base.core.bo.IBo;
import com.base.core.domain.exception.BoException;
import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.ProOrFunException;
import com.base.core.domain.exception.SaveException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * <li>创建文档 2014-09-09</li>
 * <li>服务接口实现类</li>
 * <li>使用ibatis的iterate执行批量SQL时请使用execStatement函数</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
@Service(value="serviceBase")
public class ServiceBase implements IService {
	@Resource(name="boBase")
    protected IBo iBo;

    public ServiceBase() {
    }

    public IBo getiBo() {
        return iBo;
    }

    public void setiBo(IBo iBo) {
        this.iBo = iBo;
    }

    /**
     * 原生执行指定的sqlMapId
     *
     * @param sqlMapId sqlmap的ID
     * @param param    参数对象
     * @return 返回执行结果, 需要自行转换类型
     */
    public Object execStatement(String sqlMapId, Object param) throws BoException {
        return iBo.execStatement(sqlMapId, param);
    }

    /**
     * 添加新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    @Override
    public long insert(Map mapParam) throws SaveException {
        return iBo.insert(mapParam);
    }

    /**
     * 批量添加新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    @Override
    public int insert(List listMap) throws SaveException {
        return iBo.insert(listMap);
    }

    /**
     * 批量更新纪录
     *
     * @param listMap map对象集合
     * @return 返回更新的记录数
     * @throws SaveException 出错
     */
    @Override
    public int update(List listMap) throws SaveException {
        return iBo.update(listMap);
    }

    /**
     * 添加、更新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    @Override
    public int save(Map mapParam) throws SaveException {
        return iBo.save(mapParam);
    }

    /**
     * 批量添加、更新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    @Override
    public int save(List listMap) throws SaveException {
        return iBo.save(listMap);
    }

    /**
     * 动态参数查询
     *
     * @param mapParam 包含各种查询条件,如果为null，查询所有记录
     * @return 获取查询记录
     */
    @Override
    public List select(Map mapParam) throws ProOrFunException {
        return iBo.select(mapParam);
    }

    /**
     * 删除纪录
     *
     * @param mapParam map对象
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    @Override
    public int delete(Map mapParam) throws DelErrorException {
        return iBo.delete(mapParam);
    }

    /**
     * 批量删除纪录
     *
     * @param listMap map对象集合
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    @Override
    public int delete(List listMap) throws DelErrorException {
        return iBo.delete(listMap);
    }
}

package com.base.core.service;

import com.base.core.domain.exception.BoException;
import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.ProOrFunException;
import com.base.core.domain.exception.SaveException;

import java.util.List;
import java.util.Map;

/**
 * <ol>
 * <li>创建文档 Date: 2014-08-28</li>
 * <li>服务接口</li>
 * <li>使用ibatis的iterate执行批量SQL时请使用execStatement函数</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public interface IService {
    /**
     * 原生执行指定的sqlMapId
     *
     * @param sqlMapId sqlmap的ID
     * @param param    参数对象
     * @return 返回执行结果, 需要自行转换类型
     */
    public Object execStatement(String sqlMapId, Object param) throws BoException;

    /**
     * 添加新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    public long insert(Map mapParam) throws SaveException;

    /**
     * 批量添加新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    public int insert(List listMap) throws SaveException;

    /**
     * 批量更新纪录
     *
     * @param listMap map对象集合
     * @return 返回更新的记录数
     * @throws SaveException 出错
     */
    public int update(List listMap) throws SaveException;

    /**
     * 添加、更新纪录
     *
     * @param mapParam map对象
     * @return 返回添加记录的序列, 如果没有序列返回1
     * @throws SaveException 出错
     */
    public int save(Map mapParam) throws SaveException;

    /**
     * 批量添加、更新纪录
     *
     * @param listMap map对象集合
     * @return 返回添加的记录数
     * @throws SaveException 出错
     */
    public int save(List listMap) throws SaveException;

    /**
     * 动态参数查询
     *
     * @param mapParam 包含各种查询条件,如果为null，查询所有记录
     * @return 获取查询记录
     */
    public List select(Map mapParam) throws ProOrFunException;

    /**
     * 删除纪录
     *
     * @param mapParam map对象
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    public int delete(Map mapParam) throws DelErrorException;

    /**
     * 批量删除纪录
     *
     * @param listMap map对象集合
     * @return 返回删除的记录数
     * @throws DelErrorException 出错
     */
    public int delete(List listMap) throws DelErrorException;
}

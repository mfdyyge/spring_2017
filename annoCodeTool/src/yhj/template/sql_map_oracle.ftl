<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN"
        "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<sqlMap namespace="${mapProp.sqlmap_namespace}">
    <!--
    创建日期:${date}
    描述:${tabelComments}对应的sqlmap
    作者:<a href="${mapProp.author}">${mapProp.editor}</a>
    版本:1.0
    备注:
    -->
    <cacheModel id="cache${sqlName}" type="LRU">
        <flushInterval hours="24"/>
        <flushOnExecute statement="${mapProp.sqlmap_namespace}.insert${sqlName}"/>
        <flushOnExecute statement="${mapProp.sqlmap_namespace}.update${sqlName}"/>
        <flushOnExecute statement="${mapProp.sqlmap_namespace}.delete${sqlName}"/>
        <property name="cache-size" value="50"/>
    </cacheModel>

    <!-- ${tabelComments}通用查询条件 -->
    <sql id="${sqlName}_WHERE">
        <dynamic prepend="WHERE">
        <#list tableCols as col>
        <#assign fieldSqlType='${col.fieldSqlType}'>
        <#if fieldSqlType=="DATE">
            <isNotEmpty prepend="AND" property="${col.fieldName}">
                ${colAlias}.${col.fieldName} = TO_DATE(#${col.fieldName}#,'yyyy-mm-dd')
            </isNotEmpty>
            <!--按时间段查询-->
            <isNotEmpty prepend="AND" property="MIN_${col.fieldName}">
                <![CDATA[
                ${colAlias}.${col.fieldName}  >= TO_DATE(#MIN_${col.fieldName}#,'YYYY-MM-DD hh24:mi:ss')
                ]]>
            </isNotEmpty>
            <isNotEmpty prepend="AND" property="MAX_${col.fieldName}">
                <![CDATA[
                ${colAlias}.${col.fieldName} <= TO_DATE(#MAX_${col.fieldName}#,'YYYY-MM-DD hh24:mi:ss')
                ]]>
            </isNotEmpty>
        <#else>
            <isNotEmpty prepend="AND" property="${col.fieldName}">
                ${colAlias}.${col.fieldName} = #${col.fieldName}#
            </isNotEmpty>
        </#if>
        </#list>
        </dynamic>
    </sql>
   
   <!-- ${tabelComments}动态列查询 注意map中必须有COLS对应的list的列名 -->
    <select id="selectDynamic${sqlName}" parameterClass="map" resultClass="resultHashMap"
            remapResults="true">
        <isNotEmpty property="CURPAGE">
            <isNotEmpty property="PAGESIZE">
                SELECT F.* FROM (
            </isNotEmpty>
        </isNotEmpty>
        SELECT E.*,ROWNUM ROWNO FROM (
            SELECT
            <iterate property="COLS" open="" close="" conjunction=",">
                $COLS[]$
            </iterate>
            /*统计总记录数,不受排序影响*/
            ,DECODE(SIGN(FIRST_VALUE(ROWNUM)
            OVER() - LAST_VALUE(ROWNUM) OVER()),
            -1, LAST_VALUE(ROWNUM) OVER(),
            0, FIRST_VALUE(ROWNUM) OVER(),
            1, FIRST_VALUE(ROWNUM) OVER()) AS TOTAL
            FROM ${sqlName} ${colAlias}
            <include refid="${sqlName}_WHERE"/>
        )E
        <isNotEmpty property="CURPAGE">
            <isNotEmpty property="PAGESIZE">
                <![CDATA[ WHERE rownum <=#CURPAGE# * #PAGESIZE# ) F WHERE F.ROWNO >=(#CURPAGE# - 1) * #PAGESIZE# + 1]]>
            </isNotEmpty>
        </isNotEmpty>
    </select>
   
    <!-- ${tabelComments}通用查询 -->
    <select id="select${sqlName}" parameterClass="map" resultClass="resultHashMap"
            cacheModel="cache${sqlName}">
        <isNotEmpty property="CURPAGE">
            <isNotEmpty property="PAGESIZE">
                SELECT F.* FROM (
            </isNotEmpty>
        </isNotEmpty>
        SELECT E.*,ROWNUM ROWNO FROM (
            SELECT <#list tableCols as col><#assign fieldSqlType='${col.fieldSqlType}'><#if fieldSqlType=="DATE">
            TO_CHAR(${colAlias}.${col.fieldName},'YYYY-MM-DD hh24:mi:ss') ${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#else>${colAlias}.${col.fieldName}<#if col_index+1 != tableCols?size>,</#if></#if><#if (col_index+1)%5==0>${'\r\n\t\t\t'}</#if></#list>
            /*统计总记录数,不受排序影响*/
            ,FIRST_VALUE(ROWNUM) OVER(ORDER BY ROWNUM DESC) AS TOTAL
            FROM ${sqlName} ${colAlias}
            <include refid="${sqlName}_WHERE"/>
            <isNotEmpty property="SORTNAME">
            	 ORDER BY  $SORTNAME$
            	 <isNotEmpty property="SORTORDER">
            	 	 $SORTORDER$
            	 </isNotEmpty>
            </isNotEmpty>
        )E
        <isNotEmpty property="CURPAGE">
            <isNotEmpty property="PAGESIZE">
                <![CDATA[ WHERE ROWNUM <=#CURPAGE# * #PAGESIZE# ) F WHERE F.ROWNO >=(#CURPAGE# - 1) * #PAGESIZE# + 1]]>
            </isNotEmpty>
        </isNotEmpty>
    </select>
   
    <!-- ${tabelComments}通用查询记录数    -->
    <select id="select${sqlName}Count" parameterClass="map" resultClass="java.lang.Integer"
            cacheModel="cache${sqlName}">
        SELECT COUNT(1) FROM ${sqlName} ${colAlias}
        <include refid="${sqlName}_WHERE"/>
    </select>
    
    <!-- ${tabelComments}通用添加 -->
    <insert id="insert${sqlName}" parameterClass="map">
        INSERT INTO ${sqlName}(<#list tableCols as col>${col.fieldName}<#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)
        VALUES(<#list tableCols as col><#assign fieldSqlType='${col.fieldSqlType}'><#if fieldSqlType=="DATE">SYSDATE<#else>#${col.javaAtrriber}:${col.sqlMapType}#</#if><#if col_index+1 != tableCols?size>,</#if><#if (col_index+1)%5==0 >${'\r\n\t\t\t'}</#if></#list>)
    </insert>

    <!-- ${tabelComments}通用更新 -->
    <update id="update${sqlName}" parameterClass="map">
        UPDATE ${sqlName}
        <dynamic prepend="set">
    <#list tableCols as col>
        <#assign fieldSqlType='${col.fieldSqlType}'>
        <#if fieldSqlType=="DATE">
            <isNotEmpty prepend="," property="${col.fieldName}"> ${col.fieldName} =TO_DATE(#${col.javaAtrriber}#,'YYYY-MM-DD hh24:mi:ss')</isNotEmpty>
        <#else>
            <isNotNull prepend="," property="${col.fieldName}"> ${col.fieldName} = #${col.javaAtrriber}:${col.sqlMapType}#</isNotNull>
        </#if>
    </#list>
        </dynamic>
        WHERE <#list mapPriKey?keys as col>${col} = #${col}#<#if col_index+1 != mapPriKey?keys?size> AND </#if></#list>
    </update>

    <!-- 根据主键删除${tabelComments}数据 -->
    <delete id="delete${sqlName}" parameterClass="map">
        DELETE FROM ${sqlName} ${colAlias} WHERE <#list mapPriKey?keys as col>${colAlias}.${col} = #${col}#<#if col_index+1 != mapPriKey?keys?size> AND </#if></#list>
    </delete>
</sqlMap>
